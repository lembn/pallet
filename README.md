# pallet

> Pallet allows users to quickly store and share notes on a simple desktop applicaton over a network connection.

## Design

![[notes.jpg]]

> [Tutorial](https://youtu.be/Efo7nIUF2JY)

Clicking one of these opens up a note pad of that note. The note squares have the note title and the first few lines of the note's content. On hover a red trash can delete button fades in on the bottom right.

When in notepad mode, the creator of a note can choose to set it to edtiable, allowing viewers to edit the note. There will be an animation between the notes page and the notepad editing page.

There will be a search bar at the bottom where users can search for network addresses of other user's notes.

sThe network connections will be peer to peer

## TODO:

- Note functionality:
- add delete functionality - (only works if owner)
- add privacy functionality - (only works if owner)
- add network.png - (this icon replaces the lock and trash can if not owner. on hover, this will show the address of the note as a tooltip)
- add time saved to note (read from note metadata)

- programmatically create note vboxes
- add functionality to add note btn
