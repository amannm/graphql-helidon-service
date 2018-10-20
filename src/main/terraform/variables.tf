variable "linux_admin_username" {
  type = "string"
  description = "User name for authentication to the Kubernetes linux agent virtual machines in the cluster."
}

variable "linux_admin_password" {
  type = "string"
  description = "The password for the Linux admin account."
}

variable "gcp_cluster_count" {
  type = "string"
  description = "Count of cluster instances to start."
}

variable "cluster_name" {
  type = "string"
  description = "Cluster name for the GCP Cluster."
}

variable "container_registry" {
  default = ""
}
variable "image_tag" {
  default = ""
}