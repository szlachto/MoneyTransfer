# MoneyTransfer
RESTful API for money transfers between accounts

* Application starts a jetty server on localhost port 8080
* H2 in memory database initialized with some sample user and account data

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /user/{userName} | get user by user name |
| GET | /user/all | get all users |
| PUT | /user/create | create a new user |
| DELETE | /user/{userId} | remove user |
| GET | /account/{accountId} | get account by accountId |
| GET | /account/all | get all accounts |
| GET | /account/{accountId}/balance | get account balance by accountId |
| PUT | /account/{accountId}/withdraw/{amount} | withdraw money from account |
| PUT | /account/{accountId}/deposit/{amount} | deposit money to account |
| POST | /transaction | perform transaction between 2 user accounts |