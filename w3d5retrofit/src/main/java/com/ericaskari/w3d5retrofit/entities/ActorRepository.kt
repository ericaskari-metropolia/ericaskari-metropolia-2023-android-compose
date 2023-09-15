package com.example.exercise_5.ui.member

import com.ericaskari.w3d5retrofit.entities.Actor
import com.ericaskari.w3d5retrofit.entities.ActorDao


/**
 * @author Mohammad Askari
 */
class ActorRepository(private val dao: ActorDao) {
    fun find() = dao.find()

    fun findById(id: String) = dao.findById(id)

    fun insert(vararg items: Actor) = dao.insert(*items)

    fun delete(items: Actor) = dao.delete(items)

    fun deleteAll() = dao.deleteAll()
}