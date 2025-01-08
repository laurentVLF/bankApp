output "public_ip" {
  description = "public apps ip"
  value       = aws_eip.lb.public_ip
}