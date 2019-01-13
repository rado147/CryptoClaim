# REST API of CryptoClaim

Base URL is: https://cryptoclaim.cfapps.sap.hana.ondemand.com/

| Endpoint(URI)                            	         | Request Method | Request parameters  	 | Request Body	       | Authorization  	| Description                |                                                                       
|----------------------------------------------------|----------------|--------------------------|---------------------|--------------------|-------------------------|
| /												     | GET        	  |	none	     			 | none				   | not required		| Healthcheck endpoint - returns the name of the application |
| /register			   				                 | POST           | client_id, password		 | none		 		   | not required		| "Register" with client_id and password and get the corresponding private key in return |
| /send											     | POST        	  |	client_id     			 | required, JSON	   | Bearer token		| Send a message to another client |
| /read				   				                 | POST           | client_id, message_id	 | none		 		   | Bearer token		| Read a message |
| /list											     | GET        	  |	client_id, attributes*	 | none				   | Bearer token		| List all the available messages for the user |


* attributes should be presented as comma separated values of {sendAt, id, sendingClient}

## Example responses

### Register

Example response body:

{
    "name": "user1",
    "private_key": "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQChlvB5eydtHmgIsyB+afWaCp..."
}

### Send

Example request body:

{
	"receivingClient": "user2",
	"rawData": "/s/d"
}

Example response body:

"Message send to user2"

### Read

Example response body:

{
    "sendingClient": "testname3",
    "receivingClient": "user1",
    "rawData": "/s/d",
    "sendAt": "2019-01-13T10:49:10.840+0000",
    "id": "b75aee28-4bd4-49e1-ac5a-669f6638d533"
}

### List

Example response body:

{
    "content": [
        {
            "sendingClient": "user2",
            "sendAt": "2019-01-05T13:17:35.408+0000",
            "messageId": "401153f6-f942-470c-b395-97cd6bfcfd55"
        },
        {
            "sendingClient": "testname3",
            "sendAt": "2019-01-13T10:49:10.840+0000",
            "messageId": "b75aee28-4bd4-49e1-ac5a-669f6638d533"
        }
    ],
    "pageable": {
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "pageSize": 100,
        "pageNumber": 0,
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 2,
    "last": true,
    "first": true,
    "size": 100,
    "number": 0,
    "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
    },
    "numberOfElements": 2,
    "empty": false
}


