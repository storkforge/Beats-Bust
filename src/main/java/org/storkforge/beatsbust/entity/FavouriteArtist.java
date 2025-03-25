package org.storkforge.beatsbust.entity;

import jakarta.persistence.*;

public class FavouriteArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String artistName;
}
