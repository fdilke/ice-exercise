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
    In a full production implementation we would likely use a storage service backed by a database in the cloud, and it would use queries with joins rather than the not too efficient in-memory searches.

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
(Also found an implementation in the JDK, but in an unofficial extension (?) and wouldn't run.
There's also one in Apache Commons Text, maybe I should have used that.)

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

Many of the APIs can simply be delegated to the MusicStorageService, to such an extent that
the MusicDistributionSystem implementation is really quite a thin layer. It still seems a
worthwhile separation. In a production system I'd expect that the MDS would have to wrap
other services (e.g. payment gateways) and so would be bulked up from what is currently
something of a thin passthrough layer.

The spec seems to indicate that we should keep track of ALL streamings (even short ones)
    but then only consider monetizable ones (> 30 sec) for reports.
I'm assuming that each streaming event for an individual song includes a number of seconds,
which is truncated to an integer. Also that these aren't to be coalesced or accumulated, so
if you listen to lots of tiny snippets of a song, the artist isn't credited.

I'll assume that when the system is notified of a streaming for a song, the song has been
released, i.e. is included in a release with an agreed release date lying in the past.

Since we have a concept of payments being requested and made for streamings in a given
time period (between two dates), we must record the date of each streaming.
I'm assuming this level of resolution for the streaming time is enough.

For consistency I named all the "store" methods the same way: storeSong(), storeRelease(), etc.
Briefly considered calling the one for streamings trackStreaming(), to emphasise something
about the way we record streamings. Consistency seemed better.

As implemented, there is only one ongoing sequence number, so the labelled IDs for different 
types of domain object will all have different numbers as well as different prefixes across types.
This was not strictly necessary but seems ok as an additional safeguard that also saves memory.

For a prototype, the "streamed songs report" for an artist can be a CRLF-delimited string.
I briefly considered returning a more elaborate data structure where you could step through the
rows, but a simple text report seems adequate for this situation.

I'm interpreting the spec for the report to list all streamed songs for the specified artist,
    with an indicator whether they're monetizable or not. I used a £ sign to indicate they are, else a dash.
I include all songs in the report which appear in a release for the specified artist.
In a production system, possibly some extra logic should be added here to take account of
releases which are removed or not yet confirmed for release. It's possible also that a song
could be included in releases for multiple artists.

On filing for payment, there is a requirement for the artist to be able to request this. 
So I'll add to the Artist data:
- a list of payment requests, with dates
- a list of dates when payments were made
and also add (although it's not explicitly requested) an API for the record company to update
    these when they make a payment.
Seems likely that this audit record would be of interest to both parties.

There's a certain uniformity in the withArtist(), withRelease(), updateRelease(), etc methods,
which could be refactored to eliminate repetition but at the cost of some typed functional trickery.
For consistency, I've made all the with() methods public APIs and the update() methods private,
but not add update( methods) unless they're needed in a behaviour.

When a release is removed from distribution, then rather than remove it from the storage service,
I set an additional flag so that it is not considered streamable and won't appear in searches.
This makes the process reversible and ensures compatibility with other APIs such as the streaming report.
