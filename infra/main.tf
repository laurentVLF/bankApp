provider "aws" {
  region = var.aws_region
}

terraform {
  backend "s3" {
    bucket = "my-bank-app-tfstate"
    key    = "terraform-back.tfstate"
    region = "eu-west-3"
  }
}

module "ecr" {
  source = "./ecr"
}

module "ec2" {
  source = "./ec2"
  instance_name = "ec2-my-bank-app"
  instance_type = "t2.micro"
  private_key = var.private_key
  depends_on = [module.ecr.image_pushed]
  ecr_repository_url = module.ecr.ecr_repository_url
  image_pushed = module.ecr.image_pushed
}
