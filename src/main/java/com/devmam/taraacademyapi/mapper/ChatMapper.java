// package com.devmam.taraacademyapi.mapper;

// import com.devmam.taraacademyapi.models.dto.response.ChatDto;
// import com.devmam.taraacademyapi.models.entities.Chat;
// import org.mapstruct.Mapper;
// import org.springframework.data.domain.Page;

// import java.util.List;

// @Mapper(componentModel = "spring", uses = {MessageMapper.class})
// public interface ChatMapper {
//     ChatDto toDto(Chat chat);
//     List<ChatDto> toDtoList(List<Chat> chats);

//     default Page<ChatDto> toDtoPage(Page<Chat> chats) {
//         if(chats == null) return Page.empty();
//         return chats.map(this::toDto);
//     }
// }
