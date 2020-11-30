
# iTjuana HW

  

CRUD application for users with the following stack:

  
  

**Back-End**

___

* Kotlin (+ Ktor framework)

* jasync-SQL (DB Driver)

  
  

**Front-End**

___

* Angular 11

* Angular Material

  

**Cloud (Google Cloud Platform)**

___

* AppEngine

* Cloud Run (Containerization)

* Secret Manager (for secure secrets/env vars)

* Cloud SQL (Database server)

  

**DevOps**

* Docker containers

* Docker-compose

  
  

## Build Instructions
Make sure you have Git, Docker, Docker Compose installed and running

 1. Clone this git repository
``git clone https://github.com/maumaigre/mau-itjuanahw``
 
 2. In the git repository, run this command to build and run all containers in the project.
``docker-compose up -d``
 3. This will boot up three containers:
	 * **itjuana-db** (MySQL DB) Local port: 3310 Docker port:3306
	 * **itjuana-client** (Angular served via Node.js/nginx) Local port: 4200 Docker port: 4200
	 * **itjuana-backend** (Kotlin + Ktor framework built via Gradle) Local port: 8081 Docker port: 8080

 4. You can open the front-end application on [localhost:4200](https://localhost:4200) or send API requests manually to the backend at  [localhost:8081](https://localhost:8081)

