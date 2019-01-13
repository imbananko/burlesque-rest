[![Build Status](https://travis-ci.com/imbananko/burlesque-rest.svg?token=t7U2yUF39es8uPC2DAhs&branch=master)](https://travis-ci.com/imbananko/burlesque-rest)

# Burlesque REST API

## Preamble
The Burlesque is a REST API application built with [Spark](http://sparkjava.com/) that allows you to transfer amounts of money between accounts. All the data are stored in H2 in-memory database.

## Getting started
By default the application runs on :4567 port. 

### Base REST API URL: 
{server-adress}:4567/burlesque/api/. For that moment only GET and POST request are avialable.

### CRUD resources list:

• GET /accounts/{id}
• GET /transactions/{transaction_id}
• GET /transactions/{trading_account_id}
• GET /transactions/{contra_account_id}

• POST /accounts/create (id, amount - as parameters)
• POST /transfer/ (from, to, amount - as parameters)

### Examples:

```
curl -X POST imbananko.com:4567/burlesque/api/accounts -d 'id=1&amount=400.12 - creates account with specified id and balance
curl -X GET imbananko.com:4567/burlesque/api/accounts/ - returns a JSON list of all created acccounts
curl -X POST imbananko.com:4567/burlesque/api/transfer -d 'from=1&to=2&amount=100' - perfoms a transfer between accounts
```
