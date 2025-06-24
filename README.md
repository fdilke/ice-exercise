# Music Distribution System exercise for ICE

## To build and run

    git clone https://github.com/fdilke/ice-exercise.git
    cd ice-exercise
    sbt run

This will run the "driver" program MainDriver which instantiates a
MusicDistributionService and invokes the desired behaviours, printing
out the results.

## Design decisions

This is intended as a working prototype of an MDS, so given the development
time frame (a few days) I decided to _not_ test-drive the development and
have a "main driver" program instead.

There will also undoubtedly be an only partial understanding of the domain
model, so best to document any assumptions here and make the code flexible
enough to easily allow any required changes.

## Modelling the domain

Vocabulary of concepts with provisional assumptions:

Artist: a legal entity (e.g. a band) who produce and release songs

Song: a named datum of music. Streamable, has a length.

Release: an ordered collection of songs. Typically an EP or album.

    The lifecycle of a release is that a release data is proposed and agreed, after which the songs
    become streamable. Songs can be added or removed. The system keeps track of which songs have been streamed.
    A release can be renmoved from distribution which essentially returns it to a state where there is no
    proposed (re-)release date.

Streaming: an instance of somebody listening to all or part of a song. If <= 30sec, it doesn't count towards monetization, but is still recorded for reporting purposes.

Report: an list of streamed songs.

File for payment: an artist recording a request to be paid for the songs played since the last payment date.

## Assumptions

For each streaming, we record the song and how long it was played for. 
We won't record anything about the users.

There is a behaviour to record "<artist> has been paid up to <date>".

A search will return up to a specified number of songs whose name matches a given text pattern,
    ordered by distance

We refer to domain objects by IDs which are just wrapped strings.
    The storage service allocates unique IDs which are just sequence numbers with a prefix, e.g. "artist0".

The Music Distribution Service wraps other services including a storage service which will be used via
    interfaces. There will be local memory-backed implementations for now.
    In a full production implementation we would likely use a storage service backed by a database in the cloud.

For now there is no need to store any data or actually stream anything for the songs ; we just
    record their names and how much was streamed.
    In production the songs would be stored in the storage service which would also be responsible for streaming them.

For artists, we just store the name (typically a band name) and categories,
    which are just keywords indicating the type of music.
Releases have a name, description, and optional proposed/confirmed release dates.
They also store the ID of the associated artist, which I'd expect to map to a foreign key
    in a database backed storage implementation.

Intention is that the domain objects will mostly be referred to by their IDs, which will be passed
    around for reference, and occasionally have fields loaded or modified using the storage service.

We store the artist ID for a release, but not for a song.
Conceivably songs could appear as part of multiple releases for multiple artists.

added a convenience method so you can store multiple songs in a release

We'll store the list of song ids in the data for a release
    again these are likely to map to foreign keys

Refactored to use a class Id[<CLASS>] to tag the IDs, so we can type-safely have
Id[Song] etc without repeating any code.

Refactored to use withRelease(), updateRelease(). May use this pattern with the other types.
The repetition here could be eliminated, at the cost of some type trickery,
but for now it doesn't seem worth it.

A release can have an optional proposed release date, and an agreed release date.
The intention is that at most one of these is defined, but that isn't enforced.
For simplicity I'm storing dates (such as proposed release dates) as LocalDate, so for the purposes of this
exercise I'm glossing over the management of timezones, or just assuming that all dates are
rendered locally to wherever they are being processed. This would need to be more sophisticated
for a production app operating globally.

The utility method withRelease() was initially private, but I later exposed it as part of the official API
    as it was useful at the top level.
At this point it seemed worth separating out the interface and implementation for MusicDistributionService.

Rather than implement my own Levenshtien distance algorithm from scratch, I sought inspiration from here:
https://blog.tmorris.net/posts/finding-the-levenshtein-distance-in-scala/
But that seems to use an old version of Scalaz and was not documented well enough for me to be
able to figure it out :(
The TonyM algo also did not win my confidence by apparently memoizing a function taking memos as an argument.
I found this Java code which was easier to adapt:
https://www.baeldung.com/java-levenshtein-distance#:~:text=In%20this%20article,%20we%20describe%20the%20Levenshtein
This works, but seems somewhat less efficient as it doesn't memoize anything.
An acceptable trade-off for prototype/demo purposes.
Later decided this algorithm was unacceptably slow, there is a better (memoizing) one here:
https://stackoverflow.com/questions/13564464/problems-with-levenshtein-algorithm-in-java
This gives acceptable results, at least for my small data set.

For the prototype, when matching names I'm looping over every song in the database;
a production version with millions of songs would need to be more sophisticated.
Perhaps a trie tree or some other data structure would enable a more efficient search.
Alternatively, maybe the database understands Levenshtein natively (built into the query language)
or one could write a PL/SQL method to calculate it.

Ideally the search would provide a maximum number of songs (e.g. "only return 20").
Implement this even though my example has a rather small back catalogue of songs (5).
Arguably one should make the distance calculation case-insensitive ; I didn't.
Also note that Levenshtein distance on its own might not be the best calculation ;
    a search for "Crumpets" didn't match very well with "Crumpets (Disco Remix)".

My example has a fictitious band called The Clueless Tea Boys who have a released album
"One Lump or Two?" and an unreleased EP called "Crumpets", with a total of 5 songs.
This just demonstrates that songs are not streamable until they're part of a release
whose agreed release date has passed.

