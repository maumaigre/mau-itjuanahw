.PHONY: run

buildRun:
	docker build -t itjuana-client .
	docker run -p 8080:8080 --name itjuana-client --rm itjuana-client 

run:
	docker run -p 4200:8080 --name itjuana-client --rm itjuana-client 

deploy:
	gcloud builds submit --tag gcr.io/tokyo-epoch-296923/itjuana-client
	gcloud run deploy --image gcr.io/tokyo-epoch-296923/itjuana-client --platform managed