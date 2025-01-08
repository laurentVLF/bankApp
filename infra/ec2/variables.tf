variable "instance_type" {
  type = string
  default = "t2.micro"
}

variable "instance_name" {
  type = string
  default = "my_bank_app_ec2"
}

variable "key_pair_name" {
  type = string
  default = "my_bank_app_key_pair"
}

variable "iam_instance_profile" {
  type = string
  default = "EC2_to_ECR"
}

variable "ecr_repository_url" {
  type = string
}

variable "user_name" {
  type = string
  default = "ec2-user"
}

variable "sg_name" {
  type = string
  default = "my_bank_app_security_group"
}

variable "private_key" {
  type = string
  default = "/Users/laurentvalendoff/Desktop/mybankapp/my_bank_app_key_pair.pem"
}

variable "aws_region" {
  description = "Région AWS"
  type        = string
  default     = "eu-west-3"
}

variable "ecr_repository_name" {
  description = "Nom du dépôt ECR"
  type        = string
  default     = "my_bank_app"
}

variable "image_pushed" {
  type = string
}