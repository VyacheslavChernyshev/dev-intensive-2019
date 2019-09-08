package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.data.User
import ru.skillbranch.devintensive.models.UserView
import ru.skillbranch.devintensive.utils.Utils

fun User.toUserView(): UserView {
    val nickName = Utils.transliteration(fullName())
    val initials = Utils.toInitials(firstName, lastName)
    val status = if(lastVisit == null) "Еще не был" else if (isOnline) "online" else "Последний раз был ${lastVisit.humanizeDiff()}"
    return UserView(
        id,
        fullName = fullName(),
        avatar = avatar,
        nickName = nickName,
        initials = initials,
        status = status
    )
}

fun User.fullName():String {
    return "$firstName $lastName"
}