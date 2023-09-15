package com.example.exercise_5.ui.member

import com.ericaskari.w3d5retrofit.entities.Movie
import com.ericaskari.w3d5retrofit.entities.MovieDao


/**
 * @author Mohammad Askari
 */
class MovieRepository(private val dao: MovieDao) {
    fun find() = dao.find()

    fun findById(id: String) = dao.findById(id)

    fun insert(vararg items: Movie) = dao.insert(*items)

    fun delete(items: Movie) = dao.delete(items)

    fun deleteAll() = dao.deleteAll()
}