variable "aws_region" {
  description = "Région AWS"
  type        = string
  default     = "eu-west-3"
}

variable "private_key" {
  type = string
  default = "/Users/laurentvalendoff/Desktop/mybankapp/my_bank_app_key_pair.pem"
}