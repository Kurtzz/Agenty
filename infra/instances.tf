  resource "google_compute_network" "default" {
  name                    = "agenty"
  auto_create_subnetworks = "true"
}

resource "google_compute_address" "learning-agent" {
  name = "learning-agent-ip-${count.index}"
  count = 6
}

resource "google_compute_instance" "learning-agent" {
  count = 6
  name         = "learning-agent-${count.index}"
  machine_type = "n1-standard-2"
  zone         = "europe-west1-b"

  can_ip_forward = true

  boot_disk {
    initialize_params {
      image = "ubuntu-1604-lts"
      size = 50
    }
  }

  network_interface {
    network = "${google_compute_network.default.name}"
    access_config {
      nat_ip = "${element(google_compute_address.learning-agent.*.address, count.index)}"
    }
  }

  metadata {
    ssh-keys = "ubuntu:${file("~/.ssh/id_rsa.pub")}"
  }

  # provisioner "local-exec" {
  #   command = "echo ${element(google_compute_instance.learning-agent.*.network_interface.0.address, count.index)}  >> ../environment/learning-agents-ips.txt"
  # }
}

resource "google_compute_address" "central-agent" {
  name = "central-agent-agent-ip"
}

resource "google_compute_instance" "central-agent" {
  name         = "central-agent"
  machine_type = "n1-standard-1"
  zone         = "europe-west1-b"

  can_ip_forward = true

  boot_disk {
    initialize_params {
      image = "ubuntu-1604-lts"
      size = 50
    }
  }

  network_interface {
    network = "${google_compute_network.default.name}"
    access_config {
      nat_ip = "${google_compute_address.central-agent.address}"
    }
  }

  metadata {
    ssh-keys = "ubuntu:${file("~/.ssh/id_rsa.pub")}"
  }

  # provisioner "local-exec" {
  #   command = "echo ${google_compute_instance.central-agent.network_interface.0.address}  >> ../environment/central-agent-ip.txt"
  # }
}

resource "google_compute_address" "environment-agent" {
  name = "environment-agent-agent-ip"
}

resource "google_compute_instance" "environment-agent" {
  name         = "environment-agent"
  machine_type = "n1-standard-1"
  zone         = "europe-west1-b"

  can_ip_forward = true

  boot_disk {
    initialize_params {
      image = "ubuntu-1604-lts"
      size = 50
    }
  }

  network_interface {
    network = "${google_compute_network.default.name}"
    access_config {
      nat_ip = "${google_compute_address.environment-agent.address}"
    }
  }

  metadata {
    ssh-keys = "ubuntu:${file("~/.ssh/id_rsa.pub")}"
  }
}

resource "google_compute_firewall" "agenty" {
  name    = "agenty"
  network = "${google_compute_network.default.name}"

  allow {
    protocol = "icmp"
  }

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  # source_ranges = ["0.0.0.0/0"]
}

resource "google_compute_firewall" "agenty-vpc" {
  name    = "agenty-vpc"
  network = "${google_compute_network.default.name}"

  allow {
    protocol = "icmp"
  }

  allow {
    protocol = "tcp"
    ports = ["0-65535"]
  }

  source_ranges = ["10.132.0.0/16"]
}