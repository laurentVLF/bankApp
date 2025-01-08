resource "aws_instance" "dynamic-ec2" {
  ami                    = "ami-089c89a80285075f7"
  instance_type          = var.instance_type
  vpc_security_group_ids = [aws_security_group.my_bank_app_security_group.id]
  key_name               = var.key_pair_name
  iam_instance_profile   = var.iam_instance_profile

  tags = {
    Name = var.instance_name
  }

  user_data = <<-EOF
    #!/bin/bash
    set -e

    echo 'Updating the system' >> /tmp/provision.log
    dnf update -y

    echo 'Installing Docker' >> /tmp/provision.log
    dnf install -y docker

    echo 'Starting and enabling Docker' >> /tmp/provision.log
    systemctl start docker
    systemctl enable docker

    echo 'Adding ec2-user to the docker group' >> /tmp/provision.log
    usermod -aG docker ec2-user

    echo 'Authenticating with ECR' >> /tmp/provision.log
    aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${var.ecr_repository_url} >> /tmp/provision.log 2>&1 || { echo 'ECR authentication failed' >> /tmp/provision.log; exit 1; }

    echo 'Cleaning up old Docker containers' >> /tmp/provision.log
    docker ps -aq | xargs -r docker rm -f >> /tmp/provision.log 2>&1 || echo 'No containers to remove' >> /tmp/provision.log

    echo 'Cleaning up old Docker images' >> /tmp/provision.log
    docker images -q | xargs -r docker rmi >> /tmp/provision.log 2>&1 || echo 'No images to remove' >> /tmp/provision.log

    echo 'Pulling Docker image' >> /tmp/provision.log
    docker pull ${var.ecr_repository_url}:latest >> /tmp/provision.log 2>&1 || { echo 'Failed to pull Docker image' >> /tmp/provision.log; exit 1; }

    echo 'Running Docker container' >> /tmp/provision.log
    docker run -d -p 8080:8080 ${var.ecr_repository_url}:latest >> /tmp/provision.log 2>&1 || { echo 'Failed to run Docker container' >> /tmp/provision.log; exit 1; }

    echo 'Provisioning completed successfully' >> /tmp/provision.log
  EOF

  connection {
    type        = "ssh"
    user        = var.user_name
    private_key = file(var.private_key)
    host        = self.public_ip
  }
}

resource "aws_security_group" "my_bank_app_security_group" {
  name = var.sg_name

  ingress {
    from_port   = "22"
    to_port     = "22"
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = "8080"
    to_port     = "8080"
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = "0"
    to_port     = "0"
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_eip" "lb" {
  instance = aws_instance.dynamic-ec2.id
  domain   = "vpc"
}

resource "null_resource" "http_check" {
  depends_on = [aws_instance.dynamic-ec2]

  triggers = {
    always_run = timestamp()
  }

  provisioner "local-exec" {
    command = "bash health_check.sh http://${aws_eip.lb.public_ip}:8080/"
  }
}