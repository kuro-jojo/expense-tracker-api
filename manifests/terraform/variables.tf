variable "aws_region" { default = "us-east-1" }
variable "key_name"    {
  default = "terraform"
  description = "EC2 Key Pair Name" 
}

variable "db_password" { 
  default = "password1"
  description = "RDS DB password"
  sensitive = true
}

variable "private_key_path" {
  default = "terraform.pem"
  description = "Private Key Path" 
}

variable "db_name" {
  description = "The name of the MySQL database to be created"
  type        = string
  default     = "expense_tracker"
}