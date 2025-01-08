variable "repository_name" {
  description = "Nom du dépôt ECR"
  type        = string
  default     = "my_bank_app"
}

variable "aws_region" {
  description = "Région AWS"
  type        = string
  default     = "eu-west-3"
}