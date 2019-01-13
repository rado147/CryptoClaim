# REST API of CryptoClaim

Base URL is: https://cryptoclaim.cfapps.sap.hana.ondemand.com/

| Endpoint(URI)                            	         | Request Method | Request parameters  	 | Request Body	       | Authorization  	| Description                |                                                                       
|----------------------------------------------------|----------------|--------------------------|---------------------|--------------------|-------------------------|
| /												     | GET        	  |	none	     			 | none				   | not required		| Healthcheck endpoint - returns the name of the application |
| /register			   				                 | POST           | client_id, password		 | none		 		   | not required		| "Register" with client_id and password and get the corresponding private key in return |
| /send											     | POST        	  |	client_id     			 | required, JSON*	   | Bearer token		| Send a message to another client |
| /read				   				                 | POST           | client_id, message_id	 | none		 		   | Bearer token		| Read a message |
| /list											     | GET        	  |	client_id, attributes**	 | none				   | Bearer token		| List all the available messages for the user |
