package com.zerry.flix_session.service;

import com.zerry.flix_session.dto.PatchRequest;
import com.zerry.flix_session.model.SessionData;

public interface SessionService {
    SessionData createSession(SessionData sessionData);

    SessionData getSession(String sessionId);

    SessionData updateSession(SessionData sessionData);

    void deleteSession(String sessionId);

    // TTL 갱신 (예: 사용자 재생 중인 이벤트 발생 시마다 호출)
    void renewSession(String sessionId);

}
