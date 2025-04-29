resource "aws_instance" "spring_api" {
    ami           = "ami-084568db4383264d4"
    instance_type = "t2.micro"
    subnet_id     = aws_subnet.public_a.id
    key_name      = var.key_name
    vpc_security_group_ids = [aws_security_group.spring_sg.id]

    user_data = <<-EOF
#!/bin/bash
exec > >(tee /var/log/user-data.log | logger -t user-data -s 2>/dev/console) 2>&1

# Update & install Docker on Ubuntu
apt-get update -y
apt-get install -y docker.io
systemctl start docker
systemctl enable docker

docker run -d -p 8081:8081 \
    -e SPRING_GMAIL_USERNAME="jonathankuro.pro@gmail.com" \
    -e SPRING_GMAIL_PASSWORD="zann nrfy olno samz" \
    -e SPRING_MYSQL_HOST="${aws_db_instance.mysql.address}" \
    -e SPRING_MYSQL_DB_NAME="${var.db_name}" \
    -e SPRING_MYSQL_USERNAME="kuro" \
    -e SPRING_MYSQL_PASSWORD="${var.db_password}" \
    -e SPRING_JWT_SECRET_KEY="d1c3ab74ec60d322c9c5b70e6593165d4b54c098d5d87c055b92102e5ad80df2" \
    -e SPRING_BERT_API_ENDPOINT="http://${aws_instance.python_api.private_ip}:8082/api/v1/categorize/" \
    -e SPRING_CORS_ALLOWED_ORIGINS="*" \
    kuro08/expense-tracker-api

echo "Docker containers started successfully!"
EOF
    tags = { Name = "spring-api-instance" }
}
