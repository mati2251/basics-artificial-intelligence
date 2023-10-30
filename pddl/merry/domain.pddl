(define (domain merry)
    (:requirements :adl)

    (:types
        scolor color room block
    )
    (:predicates
        (from ?x ?y - room ?c - block)
        (iscolor ?x - room ?c - block)
        (avaliablecolor ?c - scolor)
        (position ?x - room)
    )
    (:action wez
        :parameters (?c - scolor)
        :precondition (and
            (exists
                (?p - room)
                (and (iscolor ?p ?c)
                        (position ?p)))
        )
        :effect (and
            (avaliablecolor ?c)
            (not (iscolor ?p ?c))
        )
    )
    (:action idz
        :parameters (?p - room)
        :precondition (and 
            (exists (?p2 - room) (exists (?c - scolor) (and (avaliablecolor ?c) (from ?p2 ?p ?c) (position ?p2))))
        )
        :effect (and 
            (not (avaliablecolor ?c))
            (not (position ?p2))
            (position ?p)
        )
    )
)