/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.digits.sdk.android;

import android.os.CountDownTimer;
import android.os.ResultReceiver;
import android.widget.EditText;
import android.widget.TextView;

import com.twitter.sdk.android.core.SessionManager;

import static org.mockito.Mockito.mock;

public class DummyLoginCodeController extends LoginCodeController {
    private final DigitsApiClient.SdkService accountService;
    public DummyLoginCodeController(ResultReceiver resultReceiver, StateButton sendButton,
                                    InvertedStateButton resendButton,
                                    InvertedStateButton callMeButton, EditText phoneEditText,
                                    SessionManager<DigitsSession> sessionManager,
                                    DigitsClient digitsClient, String requestId,
                                    long userId, String phoneWithCountryCode, ErrorCodes errors,
                                    ActivityClassManagerImp activityClassManagerImp,
                                    DigitsEventCollector digitsEventCollector,
                                    boolean emailCollection, TextView timerTextView,
                                    DigitsEventDetailsBuilder digitsEventDetailsBuilder) {
        super(resultReceiver, sendButton, resendButton, callMeButton, phoneEditText,
                sessionManager, digitsClient, requestId, userId, phoneWithCountryCode, errors,
                activityClassManagerImp, digitsEventCollector, emailCollection, timerTextView,
                digitsEventDetailsBuilder);
        accountService = mock(DigitsApiClient.SdkService.class);
    }

    @Override
    DigitsApiClient.SdkService getAccountService(DigitsSession session) {
        return accountService;
    }

    @Override
    CountDownTimer createCountDownTimer(final int disableDurationMillis,
                                        final TextView timerText,
                                        final InvertedStateButton resentButton,
                                        final InvertedStateButton callMeButton) {
        return mock(CountDownTimer.class);
    }

    public CountDownTimer getCountDownTimer(){
        return countDownTimer;
    }

}
