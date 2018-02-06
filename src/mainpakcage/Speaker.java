package mainpakcage;

import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

/** Speak out the given string */
public class Speaker {

    /** Speak out the given string */
    public void speakString(String string) {
        try {
            SynthesizerModeDesc desc = new SynthesizerModeDesc("FreeTTS en_US general synthesizer", "general",
                    Locale.US, null, null);
            Synthesizer synthesizer = Central.createSynthesizer(desc);
            if (synthesizer == null) {
                System.exit(1);
            }
            synthesizer.allocate();
            synthesizer.resume();
            desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
            Voice voices[] = desc.getVoices();
            if(voices != null && voices.length > 0) {
                synthesizer.getSynthesizerProperties().setVoice(voices[1]);
                // Read out the speech as an argument
                synthesizer.speakPlainText(string, null);
                synthesizer.waitEngineState(0x10000L);
            }
            synthesizer.deallocate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
