output "ecr_repository_url" {
  description = "URL du dépôt ECR"
  value       = aws_ecr_repository.my_bank_app_repository.repository_url
}

output "image_pushed" {
  value = null_resource.push_docker_image.id
}
