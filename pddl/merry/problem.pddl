(define (problem merry1)
    (:domain merry)
    (:objects
        a b c d e f g h i - room
        ga pb yc bc gd rd be ge bf b2f gh rh yi bi - scolor
        niebieski czerwony zielony pomaranczowy rozowy - color
    )

    (:init
        (iscolor a ga)
        (tocolor zielony ga)
        (iscolor b pb)
        (tocolor rozowy pb)
        (iscolor c yc)
        (tocolor pomaranczowy yc)
        (iscolor c bc)
        (tocolor niebieski bc)
        (iscolor d gd)
        (tocolor zielony gd)
        (iscolor d rd)
        (tocolor czerwony rd)
        (iscolor e be)
        (tocolor niebieski be)
        (iscolor e ge)
        (tocolor zielony ge)
        (iscolor f bf)
        (tocolor niebieski bf)
        (iscolor f b2f)
        (tocolor niebieski b2f)
        (iscolor h gh)
        (tocolor zielone gh)
        (iscolor h rh)
        (tocolor czerwony rh)
        (iscolor i yi)
        (tocolor pomaranczowy yi)
        (iscolor i bi)
        (tocolor niebieski bi)


        (from a b pomaranczowy)
        (from a c czerwony)
        (from c a czerwony)
        (from c e zielony)
        (from e c zielony)
        (from b d pomaranczowy)
        (from d f zielony)
        (from f d zielony)
        (from e f niebieski)
        (from f e niebieski)
        (from e h czerwony)
        (from h e czerwony)
        (from h i niebieski)
        (from i h niebieski)
        (from i f niebieski)
        (from f i niebieski)
        (from f g rozowy)

        (position f)
    )

    (:goal
        (and
            (position h)
            (avaliablecolor bi)
        )
    )

)