(define (problem traveler)
    (:domain traveler)
    (:objects
        p1 p2 p3 p4 p5 p6 p7 - planet
        d1 d2 d3 d4 d5 d6 - teleport
    )
    (:init
        (position p1)

        (connected p1 p2 d1)
        (connected p2 p1 d1)
        (connected p1 p6 d5)
        (connected p6 p1 d5)
        (connected p2 p3 FREE)
        (connected p3 p2 FREE)
        (connected p3 p4 d3)
        (connected p4 p3 d3)
        (connected p3 p5 d4)
        (connected p5 p3 d4)
        (connected p4 p5 FREE)
        (connected p5 p4 FREE)
        (connected p6 p7 d6)
        (connected p7 p6 d6)

        (lever p1 d1)
        (lever p2 d3)
        (lever p4 d4)
        (lever p4 d1)
        (lever p5 d5)
        (lever p6 d6)

    )
    (:goal
        (and
            (position p7)
        )
    )
)