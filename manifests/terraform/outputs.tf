output "spring_api_public_ip" {
  value = "http://${aws_instance.spring_api.public_ip}:8081"
}

output "rds_endpoint" {
  value = aws_db_instance.mysql.endpoint
}