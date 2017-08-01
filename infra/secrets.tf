provider "google" {
  credentials = "${file("account.json")}"
  project     = "agenty-175717"
  region      = "europe-west1"
}