# Find Roots

An Android exercise for developers teaching how to play around with intents, activities, services and broadcast receivers

## Answer to theoretical question:

> What would you change in the code in order to let the service run for maximum 200ms in tests environments, but continue to run for 20sec max in the real app (production environment)?

Implemented my changes in CalculateRootsService:

```
    long timeoutInMiliseconds;
    if (BuildConfig.DEBUG) {
      timeoutInMiliseconds = 200;
    } else {
      timeoutInMiliseconds = TimeUnit.SECONDS.toMillis(20);
    }
```


I pledge the highest level of ethical principles in support of academic excellence.  I ensure that all of my work reflects my own abilities and not those of someone else.