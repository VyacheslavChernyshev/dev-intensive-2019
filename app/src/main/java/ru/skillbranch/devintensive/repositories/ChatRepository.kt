package ru.skillbranch.devintensive.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.utils.DataGenerator

object ChatRepository {
    private val chats = CacheManager.loadChats()

    fun loadChats(): MutableLiveData<List<Chat>> {
        return chats
    }

    fun update(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        val idx = chats.value!!.indexOfFirst{it.id == chat.id }
        if (idx == -1) return
        copy[idx] = chat
        chats.value = copy
    }

    fun find(chatId: String): Chat? {
        val idx = chats.value!!.indexOfFirst { it.id == chatId }
        return chats.value!!.getOrNull(idx)
    }
}