resource "aws_ecr_repository" "my_bank_app_repository" {
  name = var.repository_name
}

resource "null_resource" "push_docker_image" {
  depends_on = [aws_ecr_repository.my_bank_app_repository]

  provisioner "local-exec" {
    command = <<EOT
      set -e

      echo "Cleaning up previous Docker sessions..."
      docker logout ${aws_ecr_repository.my_bank_app_repository.repository_url} || true
      docker system prune -f || true

      echo "Authenticating to ECR..."
      aws ecr get-login-password --region ${var.aws_region} --debug | docker login --username AWS --password-stdin ${aws_ecr_repository.my_bank_app_repository.repository_url}

      echo "Building Docker image..."
      docker build -t ${var.repository_name} ../.

      echo "Tagging Docker image..."
      docker tag ${var.repository_name}:latest ${aws_ecr_repository.my_bank_app_repository.repository_url}:latest

      echo "Pushing Docker image to ECR..."
      docker push ${aws_ecr_repository.my_bank_app_repository.repository_url}:latest
    EOT
  }
}
