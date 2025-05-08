package com.nls.notificationservice.domain.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "template", schema = "notification")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private List<Notification> notifications;
}