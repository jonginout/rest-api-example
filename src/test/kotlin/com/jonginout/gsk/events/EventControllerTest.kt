package com.jonginout.gsk.events

import com.jonginout.gsk.base.BaseControllerTest
import org.junit.Before
import org.junit.Test
import org.springframework.hateoas.MediaTypes
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel
import org.springframework.restdocs.hypermedia.HypermediaDocumentation.links
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

class EventControllerTest : BaseControllerTest() {

    /**
     * 모든 테스트 전마다 디비 초기
     */
    @Before
    fun resetDB() {
        this.eventRepository.deleteAll()
        this.accountRepository.deleteAll()
    }

    @Test
    fun `event 만들기 성공`() {
        val body = this.generateEventRequestBody()

        this.mockMvc.perform(
            post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("state").value(EventState.BEFORE_START.name))

            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.events").exists())
            .andExpect(jsonPath("_links.update").exists())
            // .andExpect(jsonPath("_link.attendants").exists())

            .andDo(
                document("events/create",
                    links(
                        linkWithRel("self").description("셀프 링크"),
                        linkWithRel("events").description("상세 링크"),
                        linkWithRel("update").description("수정 링크")
                    ),
                    requestFields(
                        fieldWithPath("name").description("이벤트명"),
                        fieldWithPath("description").description("이벤트 설명"),
                        fieldWithPath("location").description("이벤트 장소"),
                        fieldWithPath("price").description("참가비"),
                        fieldWithPath("startAt").description("시작일"),
                        fieldWithPath("endAt").description("종료")
                    ),
                    responseFields(
                        fieldWithPath("id").description("아이디"),
                        fieldWithPath("createdAt").description("생성일시"),
                        fieldWithPath("updatedAt").description("수정일"),

                        fieldWithPath("name").description("이벤트명"),
                        fieldWithPath("description").description("이벤트 설명"),
                        fieldWithPath("location").description("이벤트 장소"),
                        fieldWithPath("price").description("참가비"),
                        fieldWithPath("startAt").description("시작일"),
                        fieldWithPath("endAt").description("종료"),
                        fieldWithPath("offline").description("오프라인 여부"),
                        fieldWithPath("state").description("상태"),
                        fieldWithPath("creator.*").description("생성자"),

                        fieldWithPath("_links.*").ignored(),
                        fieldWithPath("_links.self.*").ignored(),
                        fieldWithPath("_links.events.*").ignored(),
                        fieldWithPath("_links.update.*").ignored()
                    )
                )
            )
    }

    @Test
    fun `event 만들기 실패`() {
        val body = EventRequestBody(
            name = "test_event",
            description = "test_event_description",
            location = "seoul",
            price = 0,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().minusDays(4)
        )

        this.mockMvc.perform(
            post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andDo(print())
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `event 목록 30개의 이벤트를 10개씩 두번째 페이지 조회`() {
        (0..30).forEach {
            this.eventRepository.save(this.generateEvent(it))
        }

        this.mockMvc.perform(
            get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "id,DESC")
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.events").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.update").exists())

            .andDo(
                document("events/list",
                    links(
                        linkWithRel("self").description("셀프 링크"),
                        linkWithRel("first").description("첫 페이지 링크"),
                        linkWithRel("prev").description("이전 페이지 링크"),
                        linkWithRel("next").description("다음 페이지 링크"),
                        linkWithRel("last").description("마지막 페이지 링크")
                    ),
                    requestParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                        parameterWithName("size").description("페이지 사이즈"),
                        parameterWithName("sort").description("페이지 정렬 (예: id,desc) - 일반적으로는 id desc 고정. 두번째 정렬 조건으로 사용")
                    ),
                    responseFields(
                        fieldWithPath("_embedded.eventList[].id").description("아이"),
                        fieldWithPath("_embedded.eventList[].createdAt").description("생성일시"),
                        fieldWithPath("_embedded.eventList[].updatedAt").description("수정일시"),
                        fieldWithPath("_embedded.eventList[].name").description("이벤트명"),
                        fieldWithPath("_embedded.eventList[].description").description("이벤트 설명"),
                        fieldWithPath("_embedded.eventList[].location").description("이벤트 장소"),
                        fieldWithPath("_embedded.eventList[].price").description("참가비"),
                        fieldWithPath("_embedded.eventList[].startAt").description("시작일"),
                        fieldWithPath("_embedded.eventList[].endAt").description("종료"),
                        fieldWithPath("_embedded.eventList[].offline").description("오프라인 여부"),
                        fieldWithPath("_embedded.eventList[].state").description("상태"),
                        fieldWithPath("_embedded.eventList[].creator").description("생성자"),
                        fieldWithPath("_embedded.eventList[]._links.self.*").description("셀프세 링크"),
                        fieldWithPath("_embedded.eventList[]._links.events.*").description("상세 링"),
                        fieldWithPath("_embedded.eventList[]._links.update.*").description("수정 링크"),
                        fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                        fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                        fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                        fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                        fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                        fieldWithPath("page.size").description("페이지 사이즈"),
                        fieldWithPath("page.totalElements").description("총 개수"),
                        fieldWithPath("page.totalPages").description("총 페이지"),
                        fieldWithPath("page.number").description("현재 페이지 (0부터 시작)")
                    )
                )
            )
    }

    @Test
    fun `event 상세 조회`() {
        val event = this.eventRepository.save(this.generateEvent())

        this.mockMvc.perform(
            get("/api/events/{id}", event.id)
        )

            .andExpect(status().isOk)
            .andDo(print())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.events").exists())
            .andExpect(jsonPath("_links.update").exists())

            .andDo(
                document("events/detail",
                    links(
                        linkWithRel("self").description("셀프 링크"),
                        linkWithRel("events").description("상세 링크"),
                        linkWithRel("update").description("수정 링크")
                    ),
                    pathParameters(
                        parameterWithName("id").description("이벤트 아이디")
                    ),
                    responseFields(
                        fieldWithPath("id").description("아이디"),
                        fieldWithPath("createdAt").description("생성일시"),
                        fieldWithPath("updatedAt").description("수정일"),

                        fieldWithPath("name").description("이벤트명"),
                        fieldWithPath("description").description("이벤트 설명"),
                        fieldWithPath("location").description("이벤트 장소"),
                        fieldWithPath("price").description("참가비"),
                        fieldWithPath("startAt").description("시작일"),
                        fieldWithPath("endAt").description("종료"),
                        fieldWithPath("offline").description("오프라인 여부"),
                        fieldWithPath("state").description("상태"),
                        fieldWithPath("creator").description("생성자"),

                        fieldWithPath("_links.*").ignored(),
                        fieldWithPath("_links.self.*").ignored(),
                        fieldWithPath("_links.events.*").ignored(),
                        fieldWithPath("_links.update.*").ignored()
                    )
                )
            )
    }

    @Test
    fun `event 수정하기`() {
        val event = this.eventRepository.save(this.generateEvent())

        val body = EventUpdateRequestBody(
            name = "change_test_event_name",
            description = event.description,
            location = event.location,
            price = event.price,
            startAt = event.startAt,
            endAt = event.endAt
        )

        this.mockMvc.perform(
            put("/api/events/{id}", event.id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(body))
        )

            .andExpect(status().isOk)
            .andDo(print())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.events").exists())
            .andExpect(jsonPath("_links.update").exists())

            .andDo(
                document("events/update",
                    links(
                        linkWithRel("self").description("셀프 링크"),
                        linkWithRel("events").description("상세 링크"),
                        linkWithRel("update").description("수정 링크")
                    ),
                    pathParameters(
                        parameterWithName("id").description("이벤트 아이디")
                    ),
                    responseFields(
                        fieldWithPath("id").description("아이디"),
                        fieldWithPath("createdAt").description("생성일시"),
                        fieldWithPath("updatedAt").description("수정일"),

                        fieldWithPath("name").description("이벤트명"),
                        fieldWithPath("description").description("이벤트 설명"),
                        fieldWithPath("location").description("이벤트 장소"),
                        fieldWithPath("price").description("참가비"),
                        fieldWithPath("startAt").description("시작일"),
                        fieldWithPath("endAt").description("종료"),
                        fieldWithPath("offline").description("오프라인 여부"),
                        fieldWithPath("state").description("상태"),
                        fieldWithPath("creator").description("수정자"),

                        fieldWithPath("_links.*").ignored(),
                        fieldWithPath("_links.self.*").ignored(),
                        fieldWithPath("_links.events.*").ignored(),
                        fieldWithPath("_links.update.*").ignored()
                    )
                )
            )
    }
}
