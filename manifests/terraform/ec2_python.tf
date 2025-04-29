resource "aws_instance" "python_api" {
  ami           = "ami-084568db4383264d4"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public_a.id
  key_name      = var.key_name
  vpc_security_group_ids = [aws_security_group.spring_sg.id]

  user_data = <<-EOF
#!/bin/bash
exec > >(tee /var/log/user-data.log | logger -t user-data -s 2>/dev/console) 2>&1
sudo apt-get update -y
sudo apt-get install -y docker.io net-tools
sudo service start docker
sudo docker run -d -p 8082:8082 kuro08/bert-transaction-categorizer-api
EOF
  tags = { Name = "python-bert-categorizer-instance" }
}

