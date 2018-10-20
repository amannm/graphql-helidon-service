resource "kubernetes_replication_controller" "redis-master" {
  metadata {
    name = "graphql-helidon-service"

    labels {
      app = "graphql-helidon-service"
      role = "api"
      tier = "backend"
    }
  }

  spec {
    replicas = 3

    selector = {
      app = "graphql-helidon-service"
      role = "api"
      tier = "backend"
    }

    template {
      container {
        image = "${var.container_registry}:${var.image_tag}"
        name = "graphql-helidon-service"

        port {
          container_port = 8080
        }

        resources {
          requests {
            cpu = "100m"
            memory = "100Mi"
          }
        }
      }
    }
  }
}