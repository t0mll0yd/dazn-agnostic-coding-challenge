# Dazn Agnostic Coding Challenge

This repository contains my submission for the Dazn Agnostic coding challenge.
It is a Finagle based service written in Scala. You can find my codebase for the Node.JS specific solution [here][0].

### API

For a given user, with a USER_ID, you can add a video stream, with a STREAM_ID. You can add a maximum of 3 streams.
An assumption here is that stream IDs are unique for all combinations of content and devices on which the content is being watched.

This allows the POST request to be idempotent.

```
POST /users/USER_ID/streams/STREAM_ID
```

If the user is already registered to this stream, it will return a `200 OK` response.
Otherwise, if the user has 2 or less current streams, this will add the new stream and return a `201 CREATED` response.
Otherwise, when the user is already registered with the maximum number of streams, it will return a `409 CONFLICT` response with a message body:

```
{
    "error": {
        "code": "streams.limit.reached",
        "message": "This user already has the maximum number of concurrent streams."
    }
}
```

NB: The endpoint requires basic auth to access and uses the following credentials: `username:password ` (super secure!). Without this the
endpoint will respond with a `401 UNAUTHORIZED`.

There is also a health check endpoint, which is used by ECS (see next section) to check
that the service has been correctly deployed and started. It also returns a message which distinguishes this version
and the Node.js version of the service. This endpoint does *not* require basic auth and can be called with:

```
GET /health-check
```

### Pipeline And Deployment

There is a simple build pipeline set up for this project on Circle CI. This runs all unit tests,
and publishes the latest code from master as a Docker image to ECR (Amazon's Elastic Container Registry). This is then deployed
to ECS (Amazon's Elastic Container Service) which is available at the following URL:

http://ec2co-ecsel-nx7ihoycmsyy-225575175.us-east-2.elb.amazonaws.com:8080

Containerising the application and hosting on ECS allows for easy scalability (both manual and automatic)
which can be implemented through the ECS dashboard.

### Running the app

To run this app, you will need to have SBT installed.
First, clone the repository and compile the code:

```
git clone git@github.com:t0mll0yd/dazn-agnostic-coding-challenge.git

cd dazn-agnostic-coding-challenge

sbt compile
```

You can then run the app as follows:

```
sbt run
```

### Testing

Unit tests can be ran with SBT:

```
sbt test
```

### Further Improvements

Given more time, here are some other things I would have done:

- The basic auth should be replaced with a more sophisticated authentication mechanism, such as OAuth2.
- The console logs should be replaced with a logger that can produce ELK logs (or similar). This would allow querying, as well as the possibility of setting up alerts.
- The in-memory store for user streams should be replaced by a proper database backend. Currently, if you restart the server, everything is lost. Woops!
- Auto-generate API documentation using Swagger.


[0]: https://github.com/t0mll0yd/dazn-node-js-coding-challenge
