// Active Menu State Management
document.addEventListener('DOMContentLoaded', () => {
    const currentUrl = window.location.href;
    const menuLinks = Array.from(document.querySelectorAll('.sidebar-nav a.menu-item'));

    // Clear all active classes first to reset state during reloads
    document.querySelectorAll('.sidebar-nav .active').forEach(el => el.classList.remove('active'));

    let bestMatch = null;
    let longestMatchLength = 0;

    menuLinks.forEach(link => {
        const linkUrl = link.href;
        if (!linkUrl || linkUrl.includes('javascript:void(0)') || linkUrl.endsWith('#')) return;

        // Basic includes checking
        if (currentUrl.includes(link.getAttribute('href'))) {
            if (linkUrl.length > longestMatchLength) {
                longestMatchLength = linkUrl.length;
                bestMatch = link;
            }
        }
    });

    // Explicit override: If URL has a ?content= query, guarantee its link wins
    const urlParams = new URLSearchParams(window.location.search);
    const contentParam = urlParams.get('content');
    if (contentParam) {
        const explicitMatch = menuLinks.find(l => {
            const hrefAttr = l.getAttribute('href');
            return hrefAttr && hrefAttr !== '#' && hrefAttr !== 'javascript:void(0)' && hrefAttr.includes('content=' + contentParam);
        });
        if (explicitMatch) bestMatch = explicitMatch;
    }

    if (bestMatch) {
        // Highlight the specific item
        bestMatch.classList.add('active');

        // Handle standard submenus (chevron rotation and expansion)
        let parentLi = bestMatch.closest('.has-submenu');
        if (parentLi) {
            parentLi.classList.add('active');
            const parentToggle = parentLi.querySelector('.submenu-toggle');
            if (parentToggle) parentToggle.classList.add('active');

            const submenu = parentLi.querySelector('.submenu');
            if (submenu) submenu.style.display = 'block';
        }

        // Handle nested report groups (College, Corporate etc.)
        let reportGroup = bestMatch.closest('.report-group');
        if (reportGroup) {
            reportGroup.classList.add('active');
            reportGroup.classList.add('expanded');
            const groupLabel = reportGroup.querySelector('.report-group-label');
            if (groupLabel) groupLabel.classList.add('active');

            const sublinks = reportGroup.querySelector('.report-sublinks');
            const toggleBtn = reportGroup.querySelector('.report-group-toggle');
            if (sublinks) sublinks.style.display = 'block';
            if (toggleBtn) toggleBtn.textContent = '-';
        }
    } else {
        const dashboardLink = document.getElementById('nav-dashboard');
        if (dashboardLink) dashboardLink.classList.add('active');
    }

    // Add click event listener to manual sub-links to switch the active class dynamically
    document.querySelectorAll('.sidebar-link, .submenu-link').forEach(link => {
        link.addEventListener('click', function (e) {
            if (this.getAttribute('href') === '#') {
                e.preventDefault();
            }
            document.querySelectorAll('.sidebar-nav a').forEach(l => l.classList.remove('active'));
            this.classList.add('active');

            const parentLi = this.closest('.has-submenu');
            if (parentLi) {
                const parentToggle = parentLi.querySelector('.submenu-toggle');
                if (parentToggle) parentToggle.classList.add('active');
            }
        });
    });
});

// Submenu Toggle Logic
document.querySelectorAll('.submenu-toggle').forEach(toggle => {
    toggle.addEventListener('click', function (e) {
        e.preventDefault();
        const parentItem = this.parentElement;
        const submenu = this.nextElementSibling;

        // Close other submenus
        document.querySelectorAll('.has-submenu').forEach(item => {
            if (item !== parentItem && item.classList.contains('active') && !item.contains(parentItem)) {
                item.classList.remove('active');
                const childSubmenu = Array.from(item.children).find(child => child.classList.contains('submenu'));
                if (childSubmenu) childSubmenu.style.display = 'none';
            }
        });

        // Toggle current submenu
        parentItem.classList.toggle('active');
        if (parentItem.classList.contains('active')) {
            submenu.style.display = 'block';
        } else {
            submenu.style.display = 'none';
        }
    });
});

// Sidebar Toggle Logic
const sidebarToggle = document.getElementById('sidebar-toggle');
const sidebar = document.getElementById('sidebar');
const mainContent = document.querySelector('.main-content');

if (sidebarToggle) {
    sidebarToggle.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        mainContent.classList.toggle('full-width');
    });
}

// User Dropdown Toggle
const userDropdown = document.querySelector('.user-dropdown');
if (userDropdown) {
    userDropdown.addEventListener('click', function (e) {
        if (e.target.closest('.dropdown-menu')) return;
        e.stopPropagation();
        this.classList.toggle('show');
    });
}

// Close dropdown when clicking outside
window.addEventListener('click', () => {
    if (userDropdown) userDropdown.classList.remove('show');
});

// Report Group Nesting Toggle (+ / -)
document.querySelectorAll('.report-group-header').forEach(header => {
    header.style.cursor = 'pointer'; // Make it clear it's clickable
    header.addEventListener('click', function (e) {
        e.preventDefault();
        e.stopPropagation();

        const group = this.closest('.report-group');
        const sublinks = group.querySelector('.report-sublinks');
        const toggleBtn = group.querySelector('.report-group-toggle');

        if (sublinks.style.display === 'none' || sublinks.style.display === '') {
            sublinks.style.display = 'block';
            toggleBtn.textContent = '-';
            group.classList.add('expanded');
        } else {
            sublinks.style.display = 'none';
            toggleBtn.textContent = '+';
            group.classList.remove('expanded');
        }
    });
});

// Payment Category Auto-Fill Logic
function autoFillPaymentFields() {
    const category = document.getElementById('payment-category');
    const installments = document.getElementById('payment-installments');
    const status = document.getElementById('payment-status');
    const nextInstallmentWrapper =
        document.getElementById('next-installment-wrapper');

    if (!category || !installments || !status) return;

    const val = category.value;

    // Map: category → { installments value, status value }
    const mapping = {
        'Registration Fee': { inst: '1', stat: 'Remaining' },
        '1st Installment': { inst: '2', stat: 'Remaining' },
        '2nd Installment': { inst: '0', stat: 'Full Payment' },
        'Full Payment': { inst: '0', stat: 'Full Payment' }
    };

    const match = mapping[val];
    if (match) {
        installments.value = match.inst;
        status.value = match.stat;
    } else {
        installments.value = '';
        status.value = '';
    }

    // Hide Next Installment Date if Full Payment
    if (val === 'Full Payment' || val === '2nd Installment') {
        if (nextInstallmentWrapper) {
            nextInstallmentWrapper.style.display = 'none';
            const nextDateInput = document.getElementById('payment-nextdate');
            if (nextDateInput) nextDateInput.value = '';
        }
    } else {
        if (nextInstallmentWrapper) nextInstallmentWrapper.style.display = 'block';
    }
}
function autoFillEditPaymentFields() {
    const category = document.getElementById("edit-payment-category").value;
    const installments = document.getElementById("edit-payment-installments");
    const status = document.getElementById("edit-payment-status");
    const nextInstallmentWrapper = document.getElementById('edit-next-installment-wrapper');

    if (category === "Registration Fee") {
        installments.value = "1";
        status.value = "Remaining 2 Installments";
    } else if (category === "1st Installment") {
        installments.value = "2";
        status.value = "Remaining 1 Installment";
    } else if (category === "2nd Installment") {
        installments.value = "3";
        status.value = "Full Amount Paid";
        document.getElementById("edit-payment-nextdate").value = "";
    } else if (category === "Full Payment") {
        installments.value = "0";
        status.value = "Full Amount Paid";
        document.getElementById("edit-payment-nextdate").value = "";
    }

    // Hide Next Installment Date if Full Payment
    if (category === "Full Payment" || category === "2nd Installment") {
        if (nextInstallmentWrapper) nextInstallmentWrapper.style.display = 'none';
        const nextDateInput = document.getElementById('edit-payment-nextdate');
        if (nextDateInput) nextDateInput.value = '';
    } else {
        if (nextInstallmentWrapper) nextInstallmentWrapper.style.display = 'block';
    }
}

/**
 * Filter start/end time options based on Batch Type (Weekend vs Week Days)
 */
function updateScheduleTimeOptions(typeEl, startEl, endEl) {
    if (!typeEl || !startEl || !endEl) return;
    const type = typeEl.value;
    const weekendTimes = ["10 AM", "11 AM", "12 PM", "1 PM"];

    const filter = (select) => {
        const currentVal = select.value;
        Array.from(select.options).forEach(opt => {
            if (!opt.value) return;
            if (type === "Weekend") {
                const isAllowed = weekendTimes.includes(opt.value);
                opt.disabled = !isAllowed;
                opt.hidden = !isAllowed;
            } else {
                opt.disabled = false;
                opt.hidden = false;
            }
        });
        // Reset if current selection is now invalid
        if (type === "Weekend" && currentVal && !weekendTimes.includes(currentVal)) {
            select.value = "";
        }
    };
    filter(startEl);
    filter(endEl);
}

// Enrollment and Enquiry Logic
function fetchEnrollmentDetails() {
    const enrollmentInput = document.getElementById("payment-enrollment-id");
    const enrollmentId = enrollmentInput ? enrollmentInput.value.trim() : "";

    if (!enrollmentId) {
        document.getElementById("payment-name").value = "";
        document.getElementById("payment-mobile").value = "";
        document.getElementById("payment-email").value = "";
        document.getElementById("payment-amount").value = "";
        return;
    }

    console.log("Fetching details for:", enrollmentId);

    fetch("/admin/enrollments/details/" + encodeURIComponent(enrollmentId))
        .then(response => {
            if (!response.ok) throw new Error("Enrollment not found");
            return response.json();
        })
        .then(data => {
            console.log("Data received:", data);
            if (data) {
                document.getElementById("payment-name").value = data.name || "";
                document.getElementById("payment-mobile").value = data.mobileNo || "";
                document.getElementById("payment-email").value = data.emailId || "";
                document.getElementById("payment-amount").value = data.amount || "";
            }
        })
        .catch(error => {
            console.error("Fetch error:", error);
            // Clear fields on error
            document.getElementById("payment-name").value = "";
            document.getElementById("payment-mobile").value = "";
            document.getElementById("payment-email").value = "";
            document.getElementById("payment-amount").value = "";
        });
}

document.addEventListener('DOMContentLoaded', function () {
    // Payment Page Auto-population
    const enrollmentIdInput = document.getElementById("payment-enrollment-id");
    if (enrollmentIdInput) {
        enrollmentIdInput.addEventListener("blur", fetchEnrollmentDetails);
    }

    // Enquiry and Enrollment edit logic is now handled by event delegation below.

    // Auto-hide success alerts after 10 seconds
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert-success');
        alerts.forEach(alert => {
            if (typeof bootstrap !== 'undefined' && bootstrap.Alert) {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                if (bsAlert) bsAlert.close();
            } else {
                alert.style.transition = 'opacity 0.5s ease-out';
                alert.style.opacity = '0';
                setTimeout(() => alert.style.display = 'none', 500);
            }
        });
    }, 10000);

    // Franchise Profile Logo Preview
    const logoUploadInput = document.getElementById('logo-upload');
    const franchiseLogoPreview = document.getElementById('franchise-logo-preview');
    if (logoUploadInput && franchiseLogoPreview) {
        logoUploadInput.addEventListener('change', function () {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    franchiseLogoPreview.src = e.target.result;
                }
                reader.readAsDataURL(file);
            }
        });
    }

    // Initialize Summernote
    if (typeof $ !== 'undefined' && $.fn.summernote) {
        $('.summernote').summernote({
            placeholder: 'Type your message here...',
            tabsize: 2,
            height: 250,
            toolbar: [
                ['style', ['style']],
                ['font', ['bold', 'underline', 'clear']],
                ['color', ['color']],
                ['para', ['ul', 'ol', 'paragraph']],
                ['table', ['table']],
                ['insert', ['link', 'picture', 'video']],
                ['view', ['fullscreen', 'codeview', 'help']]
            ]
        });
    }

    // Template selection logic
    const templateSelector = document.getElementById('template-selector');
    if (templateSelector) {
        templateSelector.addEventListener('change', function () {
            const selectedOption = this.options[this.selectedIndex];
            if (selectedOption.value) {
                document.getElementById('mail-subject').value = selectedOption.getAttribute('data-subject');
                $('#mail-body').summernote('code', selectedOption.getAttribute('data-body'));
            } else {
                document.getElementById('mail-subject').value = '';
                $('#mail-body').summernote('code', '');
            }
        });
    }

    // --- EVENT DELEGATION FOR ALL MODAL TRIGGERS ---
    document.addEventListener('click', function (e) {
        // Global Diagnostic
        const targetDesc = e.target.id ? `#${e.target.id}` : (e.target.className ? `.${e.target.className.split(' ').join('.')}` : e.target.tagName);
        console.log('[Dashboard Click]', targetDesc);

        // 1. SIMPLE BROWSER CONFIRMATION DELETE
        const deleteBtn = e.target.closest('.btn-action-delete');
        if (deleteBtn) {
            e.preventDefault();
            const deleteUrl = deleteBtn.getAttribute('href');
            console.log('[Delete] Row button clicked. URL:', deleteUrl);

            if (!deleteUrl || deleteUrl === '#') {
                console.error('[Delete] No URL found on the clicked button!');
                return;
            }

            if (confirm('Are you sure you want to permanently delete this item?')) {
                console.log('[Delete] User confirmed. Navigating to:', deleteUrl);
                window.location.href = deleteUrl;
            }
            return;
        }

        // 2. COLLEGE EDIT MODAL
        const colBtn = e.target.closest('.edit-college-btn');
        if (colBtn) {
            const modalEl = document.getElementById('editCollegeModal');
            if (modalEl) {
                document.getElementById('edit-college-id').value = colBtn.getAttribute('data-id');
                document.getElementById('edit-college-name').value = colBtn.getAttribute('data-collegename');
                document.getElementById('edit-college-category').value = colBtn.getAttribute('data-category');
                document.getElementById('edit-college-location').value = colBtn.getAttribute('data-location');
                document.getElementById('edit-college-contactperson').value = colBtn.getAttribute('data-contactperson');
                document.getElementById('edit-college-person-name').value = colBtn.getAttribute('data-name');
                document.getElementById('edit-college-mobile').value = colBtn.getAttribute('data-mobile');
                document.getElementById('edit-college-email').value = colBtn.getAttribute('data-email');
                document.getElementById('edit-college-website').value = colBtn.getAttribute('data-website');
                document.getElementById('edit-college-strength').value = colBtn.getAttribute('data-strength');
                document.getElementById('edit-college-weakness').value = colBtn.getAttribute('data-weakness');
                document.getElementById('edit-college-tieups').value = colBtn.getAttribute('data-tieups');
                document.getElementById('edit-college-remarks').value = colBtn.getAttribute('data-remarks');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 4. CORPORATE EDIT MODAL
        const corpBtn = e.target.closest('.edit-corporate-btn');
        if (corpBtn) {
            const modalEl = document.getElementById('editCorporateModal');
            if (modalEl) {
                document.getElementById('edit-corporate-id').value = corpBtn.getAttribute('data-id');
                document.getElementById('edit-corporate-company').value = corpBtn.getAttribute('data-companyname');
                document.getElementById('edit-corporate-category').value = corpBtn.getAttribute('data-category');
                document.getElementById('edit-corporate-location').value = corpBtn.getAttribute('data-location');
                document.getElementById('edit-corporate-person-name').value = corpBtn.getAttribute('data-name');
                document.getElementById('edit-corporate-designation').value = corpBtn.getAttribute('data-designation');
                document.getElementById('edit-corporate-mobile').value = corpBtn.getAttribute('data-mobile');
                document.getElementById('edit-corporate-email').value = corpBtn.getAttribute('data-email');
                document.getElementById('edit-corporate-website').value = corpBtn.getAttribute('data-website');
                document.getElementById('edit-corporate-tieups').value = corpBtn.getAttribute('data-tieups');
                document.getElementById('edit-corporate-remarks').value = corpBtn.getAttribute('data-remarks');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 5. VENDOR EDIT MODAL
        const venBtn = e.target.closest('.edit-vendor-btn');
        if (venBtn) {
            const modalEl = document.getElementById('editVendorModal');
            if (modalEl) {
                document.getElementById('edit-vendor-id').value = venBtn.getAttribute('data-id');
                document.getElementById('edit-vendor-name').value = venBtn.getAttribute('data-vendorname');
                document.getElementById('edit-vendor-category').value = venBtn.getAttribute('data-category');
                document.getElementById('edit-vendor-location').value = venBtn.getAttribute('data-location');
                document.getElementById('edit-vendor-person-name').value = venBtn.getAttribute('data-name');
                document.getElementById('edit-vendor-designation').value = venBtn.getAttribute('data-designation');
                document.getElementById('edit-vendor-mobile').value = venBtn.getAttribute('data-mobile');
                document.getElementById('edit-vendor-email').value = venBtn.getAttribute('data-email');
                document.getElementById('edit-vendor-website').value = venBtn.getAttribute('data-website');
                document.getElementById('edit-vendor-remarks').value = venBtn.getAttribute('data-remarks');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 6. EMPLOYEE EDIT MODAL
        const empBtn = e.target.closest('.edit-employee-btn');
        if (empBtn) {
            const modalEl = document.getElementById('editEmployeeModal');
            if (modalEl) {
                document.getElementById('edit-employee-id').value = empBtn.getAttribute('data-id');
                document.getElementById('edit-employee-eid').value = empBtn.getAttribute('data-employeeid');
                document.getElementById('edit-employee-name').value = empBtn.getAttribute('data-name');
                document.getElementById('edit-employee-category').value = empBtn.getAttribute('data-category');
                document.getElementById('edit-employee-designation').value = empBtn.getAttribute('data-designation');
                document.getElementById('edit-employee-mobile').value = empBtn.getAttribute('data-mobile');
                document.getElementById('edit-employee-email').value = empBtn.getAttribute('data-email');
                document.getElementById('edit-employee-doj').value = empBtn.getAttribute('data-doj');
                document.getElementById('edit-employee-salary').value = empBtn.getAttribute('data-salary');
                document.getElementById('edit-employee-remarks').value = empBtn.getAttribute('data-remarks');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 7. SCHEDULE EDIT MODAL
        const schBtn = e.target.closest('.edit-schedule-btn');
        if (schBtn) {
            const modalEl = document.getElementById('editScheduleModal');
            if (modalEl) {
                document.getElementById('edit-schedule-id').value = schBtn.getAttribute('data-id');
                document.getElementById('edit-schedule-type').value = schBtn.getAttribute('data-batchtype');
                document.getElementById('edit-schedule-batchno').value = schBtn.getAttribute('data-batchno');
                document.getElementById('edit-schedule-program').value = schBtn.getAttribute('data-programname');
                document.getElementById('edit-schedule-startdate').value = schBtn.getAttribute('data-startdate');
                document.getElementById('edit-schedule-enddate').value = schBtn.getAttribute('data-enddate');
                document.getElementById('edit-schedule-start').value = schBtn.getAttribute('data-starttime');
                document.getElementById('edit-schedule-end').value = schBtn.getAttribute('data-endtime');
                document.getElementById('edit-schedule-count').value = schBtn.getAttribute('data-count');
                document.getElementById('edit-schedule-remarks').value = schBtn.getAttribute('data-remarks');

                // Trigger time filtering for edit modal
                updateScheduleTimeOptions(
                    document.getElementById('edit-schedule-type'),
                    document.getElementById('edit-schedule-start'),
                    document.getElementById('edit-schedule-end')
                );

                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 8. PAYMENT EDIT MODAL
        const payBtn = e.target.closest('.edit-payment-btn');
        if (payBtn) {
            const modalEl = document.getElementById('editPaymentModal');
            if (modalEl) {
                document.getElementById('edit-payment-id').value = payBtn.getAttribute('data-id');
                document.getElementById('edit-payment-eid').value = payBtn.getAttribute('data-enrollmentid');
                document.getElementById('edit-payment-name').value = payBtn.getAttribute('data-name');
                document.getElementById('edit-payment-mobile').value = payBtn.getAttribute('data-mobile');
                document.getElementById('edit-payment-email').value = payBtn.getAttribute('data-email');
                document.getElementById('edit-payment-category').value = payBtn.getAttribute('data-category');
                document.getElementById('edit-payment-installments').value = payBtn.getAttribute('data-installments');
                document.getElementById('edit-payment-amount').value = payBtn.getAttribute('data-amount');
                document.getElementById('edit-payment-nextdate').value = payBtn.getAttribute('data-nextdate');
                document.getElementById('edit-payment-status').value = payBtn.getAttribute('data-status');

                // Initialize visibility of next installment date
                autoFillEditPaymentFields();

                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 9. ENROLLMENT EDIT MODAL
        const enrBtn = e.target.closest('.edit-enrollment-btn');
        if (enrBtn) {
            const modalEl = document.getElementById('editEnrollmentModal');
            if (modalEl) {
                document.getElementById('edit-enr-id').value = enrBtn.getAttribute('data-id');
                document.getElementById('edit-enr-date').value = enrBtn.getAttribute('data-date');
                document.getElementById('edit-enr-name').value = enrBtn.getAttribute('data-name');
                document.getElementById('edit-enr-mobile').value = enrBtn.getAttribute('data-mobile');
                document.getElementById('edit-enr-email').value = enrBtn.getAttribute('data-email');
                document.getElementById('edit-enr-service').value = enrBtn.getAttribute('data-service');
                document.getElementById('edit-enr-amount').value = enrBtn.getAttribute('data-amount');
                document.getElementById('edit-enr-from').value = enrBtn.getAttribute('data-from');
                document.getElementById('edit-enr-to').value = enrBtn.getAttribute('data-to');
                document.getElementById('edit-enr-enrollmentid').value = enrBtn.getAttribute('data-enrollmentid');
                document.getElementById('edit-enr-onboarding').value = enrBtn.getAttribute('data-onboarding');
                document.getElementById('edit-enr-remarks').value = enrBtn.getAttribute('data-remarks');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 10. ENQUIRY EDIT MODAL
        const enqBtn = e.target.closest('.edit-enquiry-btn');
        if (enqBtn) {
            const modalEl = document.getElementById('editEnquiryModal');
            if (modalEl) {
                document.getElementById('edit-id').value = enqBtn.getAttribute('data-id');
                document.getElementById('edit-date').value = enqBtn.getAttribute('data-date');
                document.getElementById('edit-name').value = enqBtn.getAttribute('data-name');
                document.getElementById('edit-mobile').value = enqBtn.getAttribute('data-mobile');
                document.getElementById('edit-email').value = enqBtn.getAttribute('data-email');
                document.getElementById('edit-service').value = enqBtn.getAttribute('data-service');
                document.getElementById('edit-source').value = enqBtn.getAttribute('data-source');
                document.getElementById('edit-location').value = enqBtn.getAttribute('data-location');
                document.getElementById('edit-category').value = enqBtn.getAttribute('data-category');
                document.getElementById('edit-handled').value = enqBtn.getAttribute('data-handled');
                document.getElementById('edit-remarks').value = enqBtn.getAttribute('data-remarks');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 11. TEMPLATE EDIT MODAL
        const tempBtn = e.target.closest('.edit-template-btn');
        if (tempBtn) {
            const modalEl = document.getElementById('editTemplateModal');
            if (modalEl) {
                document.getElementById('edit-template-id').value = tempBtn.getAttribute('data-id');
                document.getElementById('edit-template-name').value = tempBtn.getAttribute('data-name');
                document.getElementById('edit-template-subject').value = tempBtn.getAttribute('data-subject');
                // Note: Summernote needs special handling to set content
                $('#edit-template-body').summernote('code', tempBtn.getAttribute('data-body'));
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 12. DAILY REPORT EDIT MODAL
        const reportBtn = e.target.closest('.edit-daily-report-btn');
        if (reportBtn) {
            const modalEl = document.getElementById('editDailyReportModal');
            if (modalEl) {
                document.getElementById('edit-report-id').value = reportBtn.getAttribute('data-id');
                document.getElementById('edit-report-date').value = reportBtn.getAttribute('data-date');
                document.getElementById('edit-report-enquiries').value = reportBtn.getAttribute('data-enquiries');
                document.getElementById('edit-report-enrollment').value = reportBtn.getAttribute('data-enrollment');
                document.getElementById('edit-report-revenue').value = reportBtn.getAttribute('data-revenue');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }
        // 13. INCOME EDIT MODAL
        const incBtn = e.target.closest('.edit-income-btn');
        if (incBtn) {
            const modalEl = document.getElementById('editIncomeModal');
            if (modalEl) {
                document.getElementById('edit-income-id').value = incBtn.getAttribute('data-id');
                document.getElementById('edit-income-date').value = incBtn.getAttribute('data-date');
                document.getElementById('edit-income-channel').value = incBtn.getAttribute('data-revenuechannel');
                document.getElementById('edit-income-amount').value = incBtn.getAttribute('data-amount');
                document.getElementById('edit-income-paidby').value = incBtn.getAttribute('data-paidby');
                document.getElementById('edit-income-receivedby').value = incBtn.getAttribute('data-receivedby');
                document.getElementById('edit-income-mode').value = incBtn.getAttribute('data-modeofpayment');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

        // 14. EXPENSE EDIT MODAL
        const expBtn = e.target.closest('.edit-expense-btn');
        if (expBtn) {
            const modalEl = document.getElementById('editExpenseModal');
            if (modalEl) {
                document.getElementById('edit-expense-id').value = expBtn.getAttribute('data-id');
                document.getElementById('edit-expense-date').value = expBtn.getAttribute('data-date');
                document.getElementById('edit-expense-category').value = expBtn.getAttribute('data-category');
                document.getElementById('edit-expense-amount').value = expBtn.getAttribute('data-amount');
                document.getElementById('edit-expense-paidto').value = expBtn.getAttribute('data-paidto');
                document.getElementById('edit-expense-mode').value = expBtn.getAttribute('data-modeofpayment');
                bootstrap.Modal.getOrCreateInstance(modalEl).show();
            }
            return;
        }

    });

    // --- SCHEDULE TIME FILTERING ---
    const addType = document.getElementById('schedule-batch-type');
    const addStart = document.getElementById('schedule-start-time');
    const addEnd = document.getElementById('schedule-end-time');

    function fetchAndSetBatchNo(typeVal, targetEl) {
        if (!typeVal || !targetEl) return;
        const basePath = window.location.pathname.startsWith('/employee') ? '/employee' : '/admin';
        fetch(`${basePath}/schedules/api/generate-batch-no?batchType=${encodeURIComponent(typeVal)}`)
            .then(res => res.text())
            .then(val => {
                targetEl.value = val;
            })
            .catch(err => console.error("Could not fetch batch no", err));
    }

    if (addType) {
        addType.addEventListener('change', () => {
            updateScheduleTimeOptions(addType, addStart, addEnd);
            const batchNoInput = document.getElementById('schedule-batch-no');
            if (batchNoInput) {
                fetchAndSetBatchNo(addType.value, batchNoInput);
            }
        });
    }

    const editType = document.getElementById('edit-schedule-type');
    const editStart = document.getElementById('edit-schedule-start');
    const editEnd = document.getElementById('edit-schedule-end');
    if (editType) {
        editType.addEventListener('change', () => updateScheduleTimeOptions(editType, editStart, editEnd));
    }
    const modalType = document.getElementById('modal-schedule-batch-type');
    const modalStart = document.getElementById('modal-schedule-start-time');
    const modalEnd = document.getElementById('modal-schedule-end-time');
    if (modalType) {
        modalType.addEventListener('change', () => {
            updateScheduleTimeOptions(modalType, modalStart, modalEnd);
            const modalBatchNo = document.getElementById('modal-schedule-batch-no');
            if (modalBatchNo) fetchAndSetBatchNo(modalType.value, modalBatchNo);
        });
    }
});


document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('availability-calendar');
    const basePath = window.location.pathname.startsWith('/employee') ? '/employee' : '/admin';
    if (!calendarEl) return;
    function formatTimeForForm(timeStr) {
        const [hour] = timeStr.split(":");
        let h = parseInt(hour);
        const ampm = h >= 12 ? "PM" : "AM";
        h = h % 12;
        h = h ? h : 12;
        return `${h} ${ampm}`;
    }
    function formatTimeForDisplay(timeStr) {
        const [hour, minute] = timeStr.split(":");
        let h = parseInt(hour);
        const ampm = h >= 12 ? "PM" : "AM";
        h = h % 12;
        h = h ? h : 12;
        return `${String(h).padStart(2, '0')}:${minute} ${ampm}`;
    }
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        contentHeight: 'auto',
        headerToolbar: {
            left: 'title',
            center: '',
            right: 'prev,next'
        },
        dayMaxEvents: true,
        // Highlight booked dates on calendar
        events: function (fetchInfo, successCallback, failureCallback) {
            fetch(basePath + '/schedules/api/all')
                .then(response => response.json())
                .then(data => {
                    const events = data.map(schedule => ({
                        start: schedule.startDate,
                        end: schedule.endDate
                    }));
                    successCallback(events);
                })
                .catch(error => {
                    console.error('Schedule fetch failed:', error);
                    failureCallback(error);
                });
        },
        // When user clicks a date
        dateClick: function (info) {
            // highlight selected date
            document.querySelectorAll('.fc-daygrid-day')
                .forEach(el => el.classList.remove('fc-day-selected'));
            info.dayEl.classList.add('fc-day-selected');
            const selectedDate = info.dateStr;
            window.selectedScheduleDate = selectedDate;
            fetch(`${basePath}/schedules/api/day-slots?date=${selectedDate}`)
                .then(response => response.json())
                .then(data => {
                    const bookedContainer =
                        document.getElementById('selected-date-schedule');
                    const availableContainer =
                        document.getElementById('availableSlots');
                    bookedContainer.innerHTML = '';
                    availableContainer.innerHTML = '';
                    // LEFT PANEL → BOOKED SLOTS
                    if (data.booked && data.booked.length > 0) {
                        data.booked.forEach(schedule => {
                            bookedContainer.innerHTML += `
										<div class="slot-item">
											<span class="asterisk">*</span>
											<span>
												${schedule.programName}<br>
												at ${schedule.startTime}
												to ${schedule.endTime}
											</span>
										</div>`;
                        });
                    } else {
                        bookedContainer.innerHTML = `
									<p style="color:#64748b;font-size:0.9rem;">
										No schedules for this date.
									</p>`;
                    }
                    // RIGHT PANEL → AVAILABLE SLOTS
                    if (data.available && data.available.length > 0) {
                        data.available.forEach(slot => {
                            availableContainer.innerHTML += `
										<label class="slot-radio">
											<input type="radio"
												name="slotSelect"
												value="${slot.startTime}|${slot.endTime}">
											${formatTimeForDisplay(slot.startTime)}
											to
											${formatTimeForDisplay(slot.endTime)}
										</label>`;
                        });
                    } else {
                        availableContainer.innerHTML =
                            `<li>No available slots</li>`;
                    }
                })
                .catch(error => {

                    console.error('Slot fetch failed:', error);
                });
        }
    });
    calendar.render();

    document.addEventListener("change", function (e) {
        if (e.target.name !== "slotSelect") return;
        const selectedSlot = e.target.value.split("|");
        const startTime = selectedSlot[0];
        const endTime = selectedSlot[1];
        const selectedDate = window.selectedScheduleDate;

        if (!selectedDate) return;

        // Target both Inline Form and Modal Fields
        const populateFields = (prefix = '') => {
            const sd = document.getElementById(prefix + 'schedule-start-date');
            const st = document.getElementById(prefix + 'schedule-start-time');
            const et = document.getElementById(prefix + 'schedule-end-time');
            const bt = document.getElementById(prefix + 'schedule-batch-type');

            if (sd) sd.value = selectedDate;
            if (st) st.value = formatTimeForForm(startTime);
            if (et) et.value = formatTimeForForm(endTime);

            if (bt) {
                const dateObj = new Date(selectedDate);
                const day = dateObj.getDay();
                bt.value = (day === 0 || day === 6) ? 'Weekend' : 'Week Days';
                bt.dispatchEvent(new Event('change'));
            }
        };

        // Populate Modal (Priority for automatic opening)
        populateFields('modal-');

        // Populate Inline Form (if it exists in current view)
        populateFields('');

        // Open the Modal
        const modalEl = document.getElementById('addScheduleModal');
        if (modalEl) {
            const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
            modal.show();
        }
    });
});

document.addEventListener('DOMContentLoaded', function () {
    // ...existing calendar code... (already in the file, we're adding this below it)

    // Date Restriction Logic for Schedules
    const basePath = window.location.pathname.startsWith('/employee') ? '/employee' : '/admin';
    const setupDateRestrictions = (formSelector, batchTypeSelector, sdSelector, edSelector) => {
        const forms = document.querySelectorAll(formSelector);
        forms.forEach(form => {
            const batchTypeEl = form.querySelector(batchTypeSelector);
            const startEl = form.querySelector(sdSelector);
            const endEl = form.querySelector(edSelector);

            if (!batchTypeEl || !startEl || !endEl) return;

            const validateDate = (inputEl) => {
                if (!inputEl.value) return;
                const date = new Date(inputEl.value);
                const day = date.getDay(); // 0 is Sunday, 6 is Saturday
                const type = batchTypeEl.value;

                if (type === 'Week Days' && (day === 0 || day === 6)) {
                    alert('Please select a weekday (Monday-Friday) for Week Days batch.');
                    inputEl.value = '';
                } else if (type === 'Weekend' && (day !== 0 && day !== 6)) {
                    alert('Please select a weekend (Saturday or Sunday) for Weekend batch.');
                    inputEl.value = '';
                }
            };

            batchTypeEl.addEventListener('change', () => {
                validateDate(startEl);
                validateDate(endEl);
            });

            startEl.addEventListener('change', () => validateDate(startEl));
            endEl.addEventListener('change', () => validateDate(endEl));
        });
    };

    // Apply to Add Schedule form
    setupDateRestrictions(`form[action="${basePath}/schedules/add"]`, '[name="batchType"]', '[name="startDate"]', '[name="endDate"]');

    // Apply to Edit Schedule modal
    setupDateRestrictions('#editScheduleModal form', '#edit-schedule-type', '#edit-schedule-startdate', '#edit-schedule-enddate');
});

// ===================== SERVICE INCOME PIE CHART =====================
document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('serviceIncomeChart');
    const filterEl = document.getElementById('service-income-filter');
    if (!canvas || !filterEl) return;

    const basePath = window.location.pathname.startsWith('/employee') ? '/employee' : '/admin';

    const palette = [
        '#f26522', '#4e7aec', '#10b981', '#f59e0b', '#8b5cf6',
        '#ec4899', '#06b6d4', '#14b8a6', '#84cc16', '#ef4444'
    ];

    let serviceChart = null;

    function fetchAndRenderServiceIncome(filter) {
        fetch(`${basePath}/api/service-income?filter=${filter}`)
            .then(res => res.json())
            .then(data => {
                const labels = Object.keys(data);
                const values = Object.values(data);

                if (serviceChart) {
                    serviceChart.data.labels = labels;
                    serviceChart.data.datasets[0].data = values;
                    serviceChart.data.datasets[0].backgroundColor = palette.slice(0, labels.length);
                    serviceChart.update();
                } else {
                    serviceChart = new Chart(canvas, {
                        type: 'pie',
                        data: {
                            labels: labels,
                            datasets: [{
                                data: values,
                                backgroundColor: palette.slice(0, labels.length),
                                borderWidth: 2,
                                borderColor: '#ffffff'
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    position: 'bottom',
                                    labels: {
                                        font: { size: 11, family: "'Inter', sans-serif" },
                                        color: '#475569',
                                        padding: 12,
                                        boxWidth: 12
                                    }
                                },
                                tooltip: {
                                    callbacks: {
                                        label: function (ctx) {
                                            const val = ctx.parsed;
                                            return ` ${ctx.label}: ₹${val.toLocaleString('en-IN')}`;
                                        }
                                    }
                                }
                            }
                        }
                    });
                }

                // Show empty state message if no data
                const emptyMsg = document.getElementById('service-income-empty');
                if (labels.length === 0) {
                    if (!emptyMsg) {
                        const msg = document.createElement('p');
                        msg.id = 'service-income-empty';
                        msg.style.cssText = 'text-align:center;color:#94a3b8;font-size:0.9rem;padding:2rem 0;';
                        msg.textContent = 'No enrollment data for this period.';
                        canvas.parentElement.appendChild(msg);
                    }
                    canvas.style.display = 'none';
                } else {
                    if (emptyMsg) emptyMsg.remove();
                    canvas.style.display = '';
                }
            })
            .catch(err => console.error('Service income fetch failed:', err));
    }

    // Initial load
    fetchAndRenderServiceIncome(filterEl.value);

    // On filter change
    filterEl.addEventListener('change', function () {
        fetchAndRenderServiceIncome(this.value);
    });
});

// ===================== ESTIMATED FEE COLLECTION =====================
document.addEventListener('DOMContentLoaded', function () {
    const feeTotalEl = document.getElementById('fee-total-collected');
    const feePendingFirstEl = document.getElementById('fee-pending-first');
    const feePendingSecondEl = document.getElementById('fee-pending-second');
    const feeTotalPendingEl = document.getElementById('fee-total-pending');

    if (!feeTotalEl) return;

    const basePath = window.location.pathname.startsWith('/employee') ? '/employee' : '/admin';

    function fetchAndRenderFeeCollection() {
        fetch(`${basePath}/api/fee-collection`)
            .then(res => res.json())
            .then(data => {
                // Formatting currency
                if (feeTotalEl) {
                    feeTotalEl.textContent = `₹ ${data.totalCollected.toLocaleString('en-IN')}`;
                }
                if (feePendingFirstEl) {
                    feePendingFirstEl.textContent = data.pendingFirst || 0;
                }
                if (feePendingSecondEl) {
                    feePendingSecondEl.textContent = data.pendingSecond || 0;
                }
                if (feeTotalPendingEl) {
                    feeTotalPendingEl.textContent = data.totalPending || 0;
                }
            })
            .catch(err => console.error('Fee collection fetch failed:', err));
    }

    // Initial load
    fetchAndRenderFeeCollection();
});

// --- Batch Delete Functionality ---
document.addEventListener('DOMContentLoaded', () => {
    // 1. Handle Select All Checkbox
    document.querySelectorAll('.select-all-checkbox').forEach(selectAllBtn => {
        selectAllBtn.addEventListener('change', (e) => {
            const table = e.target.closest('.data-table');
            if (!table) return;
            
            const rowCheckboxes = table.querySelectorAll('.row-checkbox');
            rowCheckboxes.forEach(cb => {
                cb.checked = e.target.checked;
            });
            
            toggleDeleteButton(table);
        });
    });

    // 2. Handle Individual Row Checkbox to toggle Delete Button
    document.querySelectorAll('table.data-table').forEach(table => {
        table.addEventListener('change', (e) => {
            if (e.target.classList.contains('row-checkbox')) {
                toggleDeleteButton(table);
                
                // Update Select All Checkbox state
                const selectAll = table.querySelector('.select-all-checkbox');
                if (selectAll) {
                    const allCheckboxes = table.querySelectorAll('.row-checkbox');
                    const allChecked = Array.from(allCheckboxes).every(cb => cb.checked);
                    const someChecked = Array.from(allCheckboxes).some(cb => cb.checked);
                    selectAll.checked = allChecked;
                    selectAll.indeterminate = someChecked && !allChecked;
                }
            }
        });
    });

    // 3. Toggle Delete Button Visibility
    function toggleDeleteButton(table) {
        // The delete button is inserted just before the table by our python script
        const container = table.previousElementSibling;
        if (!container) return;
        const deleteBtn = container.querySelector('.btn-batch-delete');
        if (!deleteBtn) return;

        const anyChecked = table.querySelectorAll('.row-checkbox:checked').length > 0;
        deleteBtn.style.display = anyChecked ? 'inline-block' : 'none';
    }

    // 4. Handle Batch Delete Click
    document.querySelectorAll('.btn-batch-delete').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const container = e.target.closest('div');
            const table = container.nextElementSibling;
            if (!table || !table.classList.contains('data-table')) return;

            const checkedRows = Array.from(table.querySelectorAll('.row-checkbox:checked'));
            if (checkedRows.length === 0) return;

            // Extract delete URLs dynamically from the delete button in the same row
            const urlsToDelete = [];
            checkedRows.forEach(cb => {
                const tr = cb.closest('tr');
                // The single delete button is an anchor with class btn-action-delete
                // Wait, it is an 'a' tag. So finding 'a.btn-action-delete' works.
                const deleteLink = tr.querySelector('a.btn-action-delete');
                if (deleteLink && deleteLink.href) {
                    urlsToDelete.push(deleteLink.href);
                }
            });

            if (urlsToDelete.length === 0) {
                alert('Could not resolve delete paths for the selected items.');
                return;
            }

            if (!confirm(`Are you sure you want to delete ${urlsToDelete.length} selected record(s)?`)) {
                return;
            }

            // Execute deletes
            btn.textContent = 'Deleting...';
            btn.disabled = true;

            try {
                // Execute standard GET requests against the delete paths
                const promises = urlsToDelete.map(url => fetch(url, { method: 'GET' }));
                await Promise.all(promises);
                
                // Show success slightly briefly or just reload instantly
                window.location.reload();
            } catch (err) {
                console.error('Error during batch delete:', err);
                alert('An error occurred during batch deletion. Some items may not have been deleted.');
                btn.textContent = 'Delete Selected';
                btn.disabled = false;
            }
        });
    });
});
