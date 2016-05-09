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

import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.SpannedString;
import android.view.View;
import android.widget.EditText;

import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PhoneNumberActivityDelegateTests extends
        DigitsActivityDelegateTests<PhoneNumberActivityDelegate> {
    CountryListSpinner spinner;
    private ArgumentCaptor<DigitsEventDetails> digitsEventDetailsArgumentCaptor;

    @Override
    public void setUp() throws Exception {
        spinner = mock(CountryListSpinner.class);
        digitsEventDetailsArgumentCaptor = ArgumentCaptor.forClass(DigitsEventDetails.class);
        super.setUp();
    }

    @Override
    public PhoneNumberActivityDelegate getDelegate() {
        return spy(new DummyPhoneNumberActivityDelegate(digitsEventCollector));
    }

    public void testIsValid() {
        final Bundle bundle = new Bundle();
        final DigitsEventDetailsBuilder digitsEventDetailsBuilder =
                new DigitsEventDetailsBuilder()
                .withAuthStartTime(1L)
                .withLanguage("lang");
        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));
        bundle.putParcelable(DigitsClient.EXTRA_EVENT_DETAILS_BUILDER, digitsEventDetailsBuilder);

        assertTrue(delegate.isValid(bundle));
    }

    public void testIsValid_missingResultReceiver() {
        final Bundle bundle = new Bundle();
        final DigitsEventDetailsBuilder digitsEventDetailsBuilder =
                new DigitsEventDetailsBuilder()
                        .withAuthStartTime(1L)
                        .withLanguage("lang");

        bundle.putParcelable(DigitsClient.EXTRA_EVENT_DETAILS_BUILDER, digitsEventDetailsBuilder);
        assertFalse(delegate.isValid(bundle));
    }

    public void testIsValid_missingDigitsMetricsAuthStartTime() {
        final Bundle bundle = new Bundle();
        final DigitsEventDetailsBuilder digitsEventDetailsBuilder =
                new DigitsEventDetailsBuilder()
                        .withAuthStartTime(1L);

        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));
        bundle.putParcelable(DigitsClient.EXTRA_EVENT_DETAILS_BUILDER, digitsEventDetailsBuilder);

        assertFalse(delegate.isValid(bundle));
    }

    public void testIsValid_missingDigitsMetricsLanguage() {
        final Bundle bundle = new Bundle();
        final DigitsEventDetailsBuilder digitsEventDetailsBuilder =
                new DigitsEventDetailsBuilder()
                        .withLanguage("lang");

        bundle.putParcelable(DigitsClient.EXTRA_RESULT_RECEIVER, new ResultReceiver(null));
        bundle.putParcelable(DigitsClient.EXTRA_EVENT_DETAILS_BUILDER, digitsEventDetailsBuilder);

        assertFalse(delegate.isValid(bundle));
    }

    public void testGetLayoutId() {
        assertEquals(R.layout.dgts__activity_phone_number, delegate.getLayoutId());
    }

    public void testSetUpCountrySpinner() {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.setUpCountrySpinner(spinner);

        verify(spinner).setOnClickListener(captorClick.capture());
        final View.OnClickListener listener = captorClick.getValue();
        listener.onClick(null);
        verify(controller).clearError();
        verify(digitsEventCollector).countryCodeClickOnPhoneScreen();
    }

    public void testOnResume() {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.eventDetailsBuilder = new DigitsEventDetailsBuilder()
                        .withLanguage("lang")
                        .withAuthStartTime(1L);

        delegate.onResume();
        verify(controller).onResume();
        verify(digitsEventCollector)
                .phoneScreenImpression(digitsEventDetailsArgumentCaptor.capture());
        final DigitsEventDetails digitsEventDetails = digitsEventDetailsArgumentCaptor.getValue();
        assertNotNull(digitsEventDetails.language);
        assertNotNull(digitsEventDetails.elapsedTimeInMillis);
    }

    @Override
    public void testSetUpTermsText() throws Exception {
        delegate.tosFormatHelper = tosFormatHelper;
        doReturn(new SpannedString("")).when(tosFormatHelper).getFormattedTerms(anyInt());
        delegate.setUpTermsText(activity, controller, textView);
        verify(tosFormatHelper).getFormattedTerms(R.string.dgts__terms_text);
        verify(textView).setText(new SpannedString(""));
    }

    public void testOnLoadComplete() {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.onLoadComplete(PhoneNumber.emptyPhone());
        verify(controller).setPhoneNumber(PhoneNumber.emptyPhone());
        verify(controller).setCountryCode(PhoneNumber.emptyPhone());
    }

    public void testOnActivityResult_notResendResult() throws Exception {
        final PhoneNumberController controller = mock(DummyPhoneNumberController.class);
        delegate.controller = controller;
        delegate.onActivityResult(ANY_REQUEST, ANY_RESULT, activity);
        verifyNoInteractions(controller);
    }

    public class DummyPhoneNumberActivityDelegate extends PhoneNumberActivityDelegate {

        public DummyPhoneNumberActivityDelegate(DigitsEventCollector digitsEventCollector) {
            super(digitsEventCollector);
        }
    }

    public class DummyPhoneNumberController extends PhoneNumberController {

        DummyPhoneNumberController(ResultReceiver resultReceiver, StateButton stateButton,
                                   EditText phoneEditText, CountryListSpinner countryCodeSpinner,
                                   TosView tosView, DigitsEventCollector digitsEventCollector,
                                   boolean emailCollection,
                                   DigitsEventDetailsBuilder eventDetailsBuilder) {
            super(resultReceiver, stateButton, phoneEditText, countryCodeSpinner,
                    tosView, digitsEventCollector, emailCollection, eventDetailsBuilder);
        }
    }
}
