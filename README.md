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
