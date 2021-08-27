package com.fumbbl.ffb.server.net;

import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionTimeoutTaskTest {

  private static final long TIMEOUT = 10000;

  private SessionTimeoutTask timeoutTask;

  @Mock
  private SessionManager sessionManager;

  @Mock
  private ServerCommunication communication;

  @Mock
  private Session activeSession;

  @Mock
  private Session timeoutSession;

  @BeforeEach
  void setUp() {
    timeoutTask = new SessionTimeoutTask(sessionManager, communication, TIMEOUT);
  }

  @Test
  void run() {
    when(sessionManager.getAllSessions()).thenReturn(new Session[]{activeSession, timeoutSession});
    when(sessionManager.getLastPing(activeSession)).then(
      (Answer<Long>) invocationOnMock -> System.currentTimeMillis() - TIMEOUT
    );
    when(sessionManager.getLastPing(timeoutSession)).then(
      (Answer<Long>) invocationOnMock -> System.currentTimeMillis() - TIMEOUT -1
    );

    timeoutTask.run();

    verify(communication).close(timeoutSession);
    verifyNoMoreInteractions(communication);
  }
}