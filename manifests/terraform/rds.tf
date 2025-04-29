resource "aws_db_subnet_group" "default" {
  name       = "db-subnet-group"
  subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_b.id]
  tags = { Name = "db-subnet-group" }
}

resource "aws_db_instance" "mysql" {
  identifier              = "backend-db"
  engine                  = "mysql"
  instance_class          = "db.t3.micro"
  allocated_storage       = 20
  username                = "kuro"
  password                = var.db_password
  skip_final_snapshot     = true
  db_subnet_group_name    = aws_db_subnet_group.default.name
  vpc_security_group_ids  = [aws_security_group.internal_sg.id]
  publicly_accessible     = false
  db_name = var.db_name 
  tags = { Name = "backend-db" }
}
