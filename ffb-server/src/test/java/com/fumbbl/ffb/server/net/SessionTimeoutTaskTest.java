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
  private ReplaySessionManager replaySessionManager;

  @Mock
  private ServerCommunication communication;

  @Mock
  private Session activeSession;

  @Mock
  private Session timeoutSession;

  @Mock
  private Session activeReplaySession;

  @Mock
  private Session timeoutReplaySession;


  @BeforeEach
  void setUp() {
    timeoutTask = new SessionTimeoutTask(sessionManager, replaySessionManager, communication, TIMEOUT);
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

    when(replaySessionManager.getAllSessions()).thenReturn(new Session[]{activeReplaySession, timeoutReplaySession});
    when(replaySessionManager.getLastPing(activeReplaySession)).then(
      (Answer<Long>) invocationOnMock -> System.currentTimeMillis() - TIMEOUT
    );
    when(replaySessionManager.getLastPing(timeoutReplaySession)).then(
      (Answer<Long>) invocationOnMock -> System.currentTimeMillis() - TIMEOUT -1
    );


    timeoutTask.run();

    verify(communication).close(timeoutSession);
    verify(communication).close(timeoutReplaySession);
    verifyNoMoreInteractions(communication);
  }
}