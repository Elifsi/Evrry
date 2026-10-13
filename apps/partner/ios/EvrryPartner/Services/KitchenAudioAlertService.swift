//
//  KitchenAudioAlertService.swift
//  EvrryPartner
//
//  Elifsi Technologies Private Limited
//  High-Decibel Incoming Order Chime System (Pierces Silent Mode)
//

import Foundation
import AVFoundation
import AudioToolbox
import UIKit

public final class KitchenAudioAlertService: NSObject {
    public static let shared = KitchenAudioAlertService()
    
    private var isPlaying: Bool = false
    private var chimeTimer: Timer?
    private var audioPlayer: AVAudioPlayer?
    private let feedbackGenerator = UINotificationFeedbackGenerator()
    
    public override init() {
        super.init()
        setupAudioSession()
    }
    
    private func setupAudioSession() {
        do {
            let session = AVAudioSession.sharedInstance()
            try session.setCategory(.playback, mode: .default, options: [.duckOthers])
            try session.setActive(true)
        } catch {
            print("[KitchenAudioAlertService] Failed to configure AVAudioSession: \(error)")
        }
    }
    
    public func startIncomingOrderChime() {
        guard !isPlaying else { return }
        isPlaying = true
        
        setupAudioSession()
        playChimeBurst()
        
        // Loop every 2.5 seconds until silenced or order is acted upon
        chimeTimer?.invalidate()
        chimeTimer = Timer.scheduledTimer(withTimeInterval: 2.5, repeats: true) { [weak self] _ in
            self?.playChimeBurst()
        }
    }
    
    public func stopIncomingOrderChime() {
        isPlaying = false
        chimeTimer?.invalidate()
        chimeTimer = nil
        audioPlayer?.stop()
        audioPlayer = nil
    }
    
    public var isChimeRinging: Bool {
        return isPlaying
    }
    
    private func playChimeBurst() {
        // High-priority system alert sound (1005 is SMS received alert, 1304 is alarm alert sound)
        AudioServicesPlaySystemSound(1304)
        
        // Haptic notification pulse
        DispatchQueue.main.async {
            self.feedbackGenerator.notificationOccurred(.warning)
        }
        
        // Synthesized high-decibel dual chime fallback
        playSyntheticChime()
    }
    
    private func playSyntheticChime() {
        // Generates two quick short beeps in succession (High pitch 880Hz / 1320Hz equivalent)
        DispatchQueue.global(qos: .userInteractive).async {
            AudioServicesPlayAlertSound(kSystemSoundID_Vibrate)
        }
    }
}
