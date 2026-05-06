package com.example.haseka.service;

import com.example.haseka.model.Room;
import com.example.haseka.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RoomService {
    @Autowired
    private RoomRepository repo;

    public List<Room> getAllRooms() { return repo.findAll(); }
    public Room getById(int id) { return repo.findById(id).orElseThrow(); }
    public void save(Room room) { repo.save(room); }
    public void delete(int id) { repo.deleteById(id); }
}
