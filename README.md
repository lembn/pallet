# pallet

> Pallet allows users to share local files over a network connection.

## Design

![[notes.jpg]]

> [Tutorial](https://youtu.be/Efo7nIUF2JY)

Clicking one of these opens up a note pad of that note. The note squares have the note title and the first few lines of the note's content. On hover a red trash can delete button fades in on the bottom right.

When in notepad mode, the creator of a note can choose to set it to edtiable, allowing viewers to edit the note. There will be an [animation](https://youtu.be/cqskg3DYH8g) between the notes page and the notepad editing page.

There will be a search bar at the bottom where users can search for network addresses of other user's notes.

The network connections will be peer to peer

## TODO

- add download path to settings
- add download arrow button next to search bar
- users can search for a note address, then download that note
- addresses are made of `[username]:[node_id]`
  - enforce unique usernames
  - when the application starts, it creates a scanner thread, which connects to other machines and maps their userames to their IP addresses. This scanner thread is constantly running to allow multiple people to join.
  - when a user requests a download, they send the note's id
  - each user has a client socket which does the scanning and sends download requests
  - each user also has a server socket which processes download requests

## ROADMAP

- In-application file editor to allow collaboration
- Create arbitrary notes instead of creating files and uploading them
