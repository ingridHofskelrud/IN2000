```mermaid
flowchart TD

    id1((Start))
    id2((End))
    id3(Search airport)
    id4(Select airport)
    id5{Found results?}
    id6(Try again?)

    id1 --> id3
    id3 --> id5
    id5 -- "Yes" --> id4
    id5 -- "No" --> id6
    id6 -- "Yes" -->id3
    id6 -- "No" -->id2
    id4 --> id2

```
