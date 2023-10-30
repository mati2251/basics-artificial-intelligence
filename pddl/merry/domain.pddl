(define (domain merry)
    (:requirements :adl)

    (:types
        scolor color room block
    )
    (:predicates
        (from ?x ?y - room ?c - block)
        (iscolor ?x - room ?c - block)
        (avaliablecolor ?c - scolor)
        (tocolor ?c - color ?sc - scolor)
        (position ?x - room)
    )
    (:action wez
        :parameters (?c - color)
        :precondition (and
            (exists
                (?p - room)
                (exists
                    (?sc)
                    (and
                        (tocolor ?c ?sc)
                        (iscolor ?p ?sc)
                        (position ?p))))
        )
        :effect (and
            (avaliablecolor ?sc)
            (not (iscolor ?p ?sc))
        )
    )
    (:action idz
        :parameters (?p - room)
        :precondition (and
            (exists
                (?p2 - room)
                (exists
                    (?sc - scolor)
                    (exists
                        (?c - color)
                        (and
                            (avaliablecolor ?sc)
                            (tocolor ?c ?sc)
                            (from ?p2 ?p ?c)
                            (position ?p2))))
            )
        )
        :effect (and
            (not (avaliablecolor ?sc))
            (not (position ?p2))
            (position ?p)
        )
    )
)