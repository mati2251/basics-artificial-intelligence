(define (domain traveler)
    (:requirements :adl)
    (:types
        planet teleport
    )

    (:constants
        FREE - teleport
    )
    (:predicates
        (position ?p - planet)
        (connected ?p1 ?p2 - planet ?t - teleport)
        (available ?t - teleport)
        (lever ?p - planet ?t - teleport)
    )
    (:action move
        :parameters (?p - planet)
        :precondition (and
            (exists
                (?p2 - planet)
                (position ?p2))
            (exists
                (?t)
                (connected ?p2 ?p ?t)
            )
            (or
                (available ?t)
                (= ?t FREE))

        )
        :effect (and
            (not (position ?p2))
            (position ?p)
            (not (available ?t))
        )
    )

    (:action openteleport
        :parameters (?p - planet ?t - teleport)
        :precondition (and
            (lever ?p ?t)
            (position ?p)
            (not (available ?t))
        )
        :effect (and
            (available ?t)
        )
    )
)