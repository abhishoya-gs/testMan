variable "aws_region" {}

provider "aws" {
  region = var.aws_region
}

variable "vpc_cidr" {}

resource "aws_vpc" "redshift_vpc" {
  cidr_block       = var.vpc_cidr
  instance_tenancy = "default"
  tags = {
    Name = "redshift-vpc"
  }
}

resource "aws_internet_gateway" "redshift_vpc_gw" {
  vpc_id = aws_vpc.redshift_vpc.id
  depends_on = [
    aws_vpc.redshift_vpc
  ]
}

resource "aws_default_route_table" "example" {
  default_route_table_id = aws_vpc.redshift_vpc.default_route_table_id
  route = [
    {
      cidr_block = "0.0.0.0/0"
      gateway_id = aws_internet_gateway.redshift_vpc_gw.id
      egress_only_gateway_id    = ""
      instance_id               = ""
      nat_gateway_id            = ""
      network_interface_id      = ""
      transit_gateway_id        = ""
      vpc_peering_connection_id = ""
      carrier_gateway_id = ""
      destination_prefix_list_id = ""
      ipv6_cidr_block = ""
      local_gateway_id = ""
      vpc_endpoint_id = ""
    }
  ]
  depends_on = [
    aws_vpc.redshift_vpc
  ]
}

variable "ip" {}

resource "aws_default_security_group" "redshift_security_group" {
  vpc_id = aws_vpc.redshift_vpc.id
  ingress {
    from_port   = 5439
    to_port     = 5439
    protocol    = "tcp"
    cidr_blocks = [var.ip]
  }
  egress {
    from_port   = 5439
    to_port     = 5439
    protocol    = "tcp"
    cidr_blocks = [var.ip]
  }
  tags = {
    Name = "redshift-sg"
  }
  depends_on = [
    aws_vpc.redshift_vpc
  ]
}

variable "redshift_subnet_cidr_1" {}
variable "redshift_subnet_cidr_2" {}

resource "aws_subnet" "redshift_subnet_1" {
  vpc_id                  = aws_vpc.redshift_vpc.id
  cidr_block              = var.redshift_subnet_cidr_1
  availability_zone       = "us-east-2a"
  map_public_ip_on_launch = "true"
  tags = {
    Name = "redshift-subnet-1"
  }
  depends_on = [
    aws_vpc.redshift_vpc
  ]
}

resource "aws_subnet" "redshift_subnet_2" {
  vpc_id                  = aws_vpc.redshift_vpc.id
  cidr_block              = var.redshift_subnet_cidr_2
  availability_zone       = "us-east-2b"
  map_public_ip_on_launch = "true"
  tags = {
    Name = "redshift-subnet-2"
  }
  depends_on = [
    aws_vpc.redshift_vpc
  ]
}

resource "aws_redshift_subnet_group" "redshift_subnet_group" {
  name       = "redshift-subnet-group"
  subnet_ids = ["${aws_subnet.redshift_subnet_1.id}", "${aws_subnet.redshift_subnet_2.id}"]
  tags = {
    environment = "dev"
    Name        = "redshift-subnet-group"
  }
}

output "redshift_endpoint" {
  value = aws_redshift_cluster.default.endpoint
}

variable "rs_cluster_identifier" {}
variable "rs_database_name" {}
variable "rs_master_username" {}
variable "rs_master_pass" {}
variable "rs_nodetype" {}
variable "rs_cluster_type" {}

resource "aws_redshift_cluster" "default" {
  cluster_identifier        = var.rs_cluster_identifier
  database_name             = var.rs_database_name
  master_username           = var.rs_master_username
  master_password           = var.rs_master_pass
  node_type                 = var.rs_nodetype
  cluster_type              = var.rs_cluster_type
  cluster_subnet_group_name = aws_redshift_subnet_group.redshift_subnet_group.id
  skip_final_snapshot       = true
  depends_on = [
    aws_vpc.redshift_vpc,
    aws_default_security_group.redshift_security_group,
    aws_redshift_subnet_group.redshift_subnet_group,
    aws_internet_gateway.redshift_vpc_gw
  ]
}