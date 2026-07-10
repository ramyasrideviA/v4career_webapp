document.addEventListener('DOMContentLoaded', () => {
    const getOtpBtn = document.getElementById('getOtpBtn');
    const verifyOtpBtn = document.getElementById('verifyOtpBtn');
    const emailInput = document.getElementById('emailId');
    const otpGroup = document.getElementById('otpGroup');
    const otpInput = document.getElementById('otp');
    const otpStatus = document.getElementById('otpStatus');
    const verifiedInput = document.getElementById('verified');
    const signupBtn = document.getElementById('signupBtn');
    
    if (getOtpBtn) {
        getOtpBtn.addEventListener('click', () => {
            const email = emailInput.value;
            if (!email) {
                alert('Please enter your email first.');
                return;
            }
            
            getOtpBtn.disabled = true;
            getOtpBtn.textContent = 'Sending...';
            
            const type = getOtpBtn.getAttribute('data-type') || 'signup';
            
            const formData = new URLSearchParams();
            formData.append('email', email);
            formData.append('type', type);

            fetch('/send-otp', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData
            })
            .then(response => {
                if (response.ok) {
                    otpGroup.style.display = 'flex';
                    getOtpBtn.textContent = 'Sent';
                } else {
                    return response.text().then(text => { throw new Error(text) });
                }
            })
            .catch(error => {
                alert(error.message || 'Failed to send OTP.');
                getOtpBtn.disabled = false;
                getOtpBtn.textContent = 'Get OTP';
            });
        });
    }

    if (verifyOtpBtn) {
        verifyOtpBtn.addEventListener('click', () => {
            const email = emailInput.value;
            const otp = otpInput.value;
            
            if (!otp) {
                alert('Please enter OTP.');
                return;
            }

            const formData = new URLSearchParams();
            formData.append('email', email);
            formData.append('otp', otp);

            fetch('/verify-otp', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData
            })
            .then(response => {
                if (response.ok) {
                    otpStatus.textContent = 'OTP Verified successfully!';
                    otpStatus.className = 'status-msg status-success';
                    verifiedInput.value = 'true';
                    signupBtn.disabled = false;
                    verifyOtpBtn.disabled = true;
                    otpInput.disabled = true;
                    emailInput.readOnly = true;
                } else {
                    otpStatus.textContent = 'Invalid OTP. Please try again.';
                    otpStatus.className = 'status-msg status-error';
                }
            })
            .catch(error => {
                otpStatus.textContent = 'Error verifying OTP.';
                otpStatus.className = 'status-msg status-error';
            });
        });
    }

    // --- Alert Messaging Logic ---
    // Auto-hide success and error alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-success, .alert-error');
    alerts.forEach(alert => {
        // Set a timeout of 5 seconds (5000ms)
        setTimeout(() => {
            alert.style.transition = 'opacity 0.6s ease-out, transform 0.4s ease-out';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            
            // Remove from DOM after transition completes
            setTimeout(() => {
                alert.style.display = 'none';
            }, 600);
        }, 5000);
    });
});
