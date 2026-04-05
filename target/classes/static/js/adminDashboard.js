document.addEventListener("DOMContentLoaded", function () {
    function validateForm() {
        let userId = document.getElementById("userId").value.trim();
        let userFullName = document.getElementById("userFullName").value.trim();
        let contact = document.getElementById("contact").value.trim();
        let userEmail = document.getElementById("userEmail").value.trim();

        if (!userId || !userFullName || !contact || !userEmail) {
            alert("Please fill in all required fields.");
            return false;
        }

        return true;
    }

    const userTable = document.getElementsByClassName('user_table');
    const buttonTable = document.querySelectorAll('.admin_navBtn');

    // BUTTON ORDER in HTML:
    //   [0] = Patient
    //   [1] = Doctor
    //   [2] = Admin
    //   [3] = Pharmacist
    //   [4] = Clinic Assistant
    //   [5] = Assigned Patient

    // TABLE ORDER in HTML:
    //   [0] = patient
    //   [1] = doctor
    //   [2] = pharmacist
    //   [3] = clinic_assistant
    //   [4] = admin
    //   [5] = assigned

    // tabMap = which BUTTON index to highlight
    const tabMap = {
        "patient":          0,
        "doctor":           1,
        "admin":            2,
        "pharmacist":       3,
        "clinic_assistant": 4,
        "assigned":         5
    };

    // tableIndexMap = which TABLE index to show
    const tableIndexMap = {
        "patient":          0,
        "doctor":           1,
        "admin":            4,
        "pharmacist":       2,
        "clinic_assistant": 3,
        "assigned":         5
    };

    // Auto-select tab based on ?tab= parameter in URL
    const urlParams = new URLSearchParams(window.location.search);
    const currentTab = urlParams.get("tab") || "patient";

    const activeTabIndex   = tabMap[currentTab];
    const activeTableIndex = tableIndexMap[currentTab];

    if (activeTabIndex !== undefined) {
        for (let i = 0; i < userTable.length; i++) {
            userTable[i].classList.remove('tableActive');
            buttonTable[i].classList.remove('admin_navBtn_active');
        }
        buttonTable[activeTabIndex].classList.add("admin_navBtn_active");
        userTable[activeTableIndex].classList.add("tableActive");
    }

    function toggleSkills() {
        let itemClass = this.className;

        for (let i = 0; i < userTable.length; i++) {
            userTable[i].className = 'user_table';
            buttonTable[i].classList.remove('admin_navBtn_active');
        }

        if (itemClass.includes('patientBtn')) {
            userTable[0].classList.add("tableActive");
        } else if (itemClass.includes('doctorBtn')) {
            userTable[1].classList.add("tableActive");
        } else if (itemClass.includes('adminBtn')) {
            userTable[4].classList.add("tableActive");
        } else if (itemClass.includes('pharmacistBtn')) {
            userTable[2].classList.add("tableActive");
        } else if (itemClass.includes('clinicAssistantBtn')) {
            userTable[3].classList.add("tableActive");
        } else if (itemClass.includes('asspatBtn')) {
            userTable[5].classList.add("tableActive");
        }

        this.classList.add("admin_navBtn_active");
    }

    buttonTable.forEach((el) => {
        el.addEventListener('click', toggleSkills);
    });

    // extraForm[0] = Patient fields
    // extraForm[1] = Doctor fields
    // extraForm[2] = Pharmacist fields
    // extraForm[3] = Clinic Assistant fields
    const userForm = document.getElementsByClassName('extraForm');

    // radioFormInput: [0]=ADMIN [1]=PATIENT [2]=DOCTOR [3]=PHARMACIST [4]=CLINIC_ASSISTANT
    const radioFormInput = document.querySelectorAll('input[type=radio][name="role"]');

    function toggleForm() {
        for (let i = 0; i < userForm.length; i++) {
            userForm[i].className = 'extraForm form-group';
        }

        if (this.classList.contains('patient')) {
            userForm[0].classList.add("activeForm");
        } else if (this.classList.contains('doctor')) {
            userForm[1].classList.add("activeForm");
        } else if (this.classList.contains('pharmacist')) {
            userForm[2].classList.add("activeForm");
        } else if (this.classList.contains('clinicAssistant')) {
            userForm[3].classList.add("activeForm");
        }
    }

    radioFormInput.forEach((e) => {
        e.addEventListener('click', toggleForm);
    });

    const confirmAddUserBtn   = document.getElementById("confirmAddUserBtn"),
          cancelAddUserBtn    = document.getElementById("cancelBtnAddUser"),
          addUserBtn          = document.getElementById("addUserBtn"),
          userListBtn         = document.getElementById("userListBtn"),
          deleteUserBtn       = document.querySelectorAll(".deleteUserBtn"),
          confirmDeleteUserBtn= document.getElementById("confirmDeleteUserBtn"),
          cancelDeleteUserBtn = document.getElementById("cancelDeleteUserBtn"),
          editUserBtn         = document.querySelectorAll(".editUserBtn");

    // User List button - show main content
    if (userListBtn) {
        userListBtn.addEventListener("click", function (e) {
            e.preventDefault();
            document.getElementsByClassName("admin_main_content")[0].style.display = "block";
            document.getElementsByClassName("add_user_page")[0].classList.remove("user_page_active");
        });
    }

    // Add User button
    addUserBtn.addEventListener("click", function (e) {
        e.preventDefault();
        document.getElementById("userFormTitle").textContent = "Add User";
        document.getElementsByClassName("add_user_page")[0].classList.add("user_page_active");
        document.getElementsByClassName("admin_main_content")[0].style.display = "none";
        document.getElementById("action").value = "add";
        document.getElementById("adduser").action = "/admin/adduser";
    });

    confirmAddUserBtn.addEventListener("click", function () {
        if (validateForm()) {
            const form = document.getElementById("adduser");
            form.submit();
        }
    });

    cancelAddUserBtn.addEventListener("click", function () {
        document.getElementsByClassName("add_user_page")[0].classList.remove("user_page_active");
        document.getElementsByClassName("admin_main_content")[0].style.display = "block";
        activeRadioForm();

        document.getElementById("userId").value                  = "";
        document.getElementById("userFullName").value            = "";
        document.getElementById("userPassword").value            = "";
        document.getElementById("userEmail").value               = "";
        document.getElementById("contact").value                 = "";
        document.getElementById("address").value                 = "";
        document.getElementById("emergencyContact").value        = "";
        document.getElementById("sensorId").value                = "";
        document.getElementById("doctorHospital").value          = "";
        document.getElementById("doctorPosition").value          = "";
        document.getElementById("pharmacistHospital").value      = "";
        document.getElementById("pharmacistPosition").value      = "";
        document.getElementById("clinicAssistantClinic").value   = "";
        document.getElementById("clinicAssistantPosition").value = "";
        document.getElementById("userId").readOnly               = false;
        document.getElementById("userPassword").readOnly         = false;
    });

    function activeRadioForm() {
        const radioFormBtn = document.querySelectorAll('.radioBtn');
        radioFormBtn.forEach((btn) => {
            btn.classList.remove("radioBtn_hide");
        });
    }

    function hideRadioForm() {
        const radioFormBtn = document.querySelectorAll('.radioBtn');
        radioFormBtn.forEach((btn) => {
            btn.classList.add("radioBtn_hide");
        });
    }

    // Delete logic
    deleteUserBtn.forEach((e) => {
        e.addEventListener("click", function () {
            document.getElementsByClassName("confirmation_deleteUser_page")[0].classList.add("user_page_active");
            const row = this.closest("tr");
            const userIdCell = row.querySelector('[data-column="userId"]');
            let role = "";

            if (row.closest("#patientTable")) {
                role = "PATIENT";
            } else if (row.closest("#doctorTable")) {
                role = "DOCTOR";
            } else if (row.closest("#pharmacistTable")) {
                role = "PHARMACIST";
            } else if (row.closest("#clinicAssistantTable")) {
                role = "CLINIC_ASSISTANT";
            } else if (row.closest("#adminTable")) {
                const roleCell = row.querySelector('[data-column="role"]');
                role = roleCell ? roleCell.innerText.trim().toUpperCase() : "ADMIN";
            }

            document.getElementById("userIdToBeDelete").value   = userIdCell ? userIdCell.innerText.trim() : '';
            document.getElementById("userRoleToBeDelete").value = role;
        });
    });

    confirmDeleteUserBtn.addEventListener("click", function () {
        document.getElementById("deleteUserForm").submit();
    });

    cancelDeleteUserBtn.addEventListener("click", function () {
        document.getElementsByClassName("confirmation_deleteUser_page")[0].classList.remove("user_page_active");
        document.getElementById("userIdToBeDelete").value = "";
    });

    // Edit logic
    editUserBtn.forEach((e) => {
        e.addEventListener("click", hideRadioForm);
        e.addEventListener("click", function () {
            const row = this.closest("tr");
            const cells = row.getElementsByTagName("td");

            document.getElementById("userId").value       = cells[0].innerText;
            document.getElementById("userId").readOnly    = true;
            document.getElementById("userFullName").value = cells[1].innerText;
            document.getElementById("contact").value      = cells[2].innerText;
            document.getElementById("userEmail").value    = cells[3].innerText;

            const editClassName = this.className;

            if (editClassName.includes('editPatient')) {
                userForm[0].classList.add("activeForm");
                radioFormInput[1].checked = true;
                document.getElementById("emergencyContact").value = cells[4].innerText;
                document.getElementById("address").value          = cells[5].innerText;
                document.getElementById("sensorId").value         = cells[6].innerText;
            } else if (editClassName.includes('editDoctor')) {
                userForm[1].classList.add("activeForm");
                radioFormInput[2].checked = true;
                document.getElementById("doctorHospital").value = cells[4].innerText;
                document.getElementById("doctorPosition").value = cells[5].innerText;
            } else if (editClassName.includes('editPharmacist')) {
                userForm[2].classList.add("activeForm");
                radioFormInput[3].checked = true;
                document.getElementById("pharmacistHospital").value = cells[4].innerText;
                document.getElementById("pharmacistPosition").value  = cells[5].innerText;
            } else if (editClassName.includes('editClinicAssistant')) {
                userForm[3].classList.add("activeForm");
                radioFormInput[4].checked = true;
                document.getElementById("clinicAssistantClinic").value   = cells[4].innerText;
                document.getElementById("clinicAssistantPosition").value = cells[5].innerText;
            } else {
                radioFormInput[0].checked = true;
            }

            document.getElementById("adduser").action = "/admin/edituser";
            document.getElementById("userFormTitle").textContent = "Edit User";
            document.getElementsByClassName("add_user_page")[0].classList.add("user_page_active");
            document.getElementsByClassName("admin_main_content")[0].style.display = "none";
            document.getElementById("action").value = "update";
        });
    });

    // Search logic
    function handleSearchInput(tableId, query) {
        const tableRows = document.getElementById(tableId).getElementsByTagName('tr');
        for (const row of tableRows) {
            const cells = row.getElementsByTagName('td');
            let found = false;
            for (const cell of cells) {
                const columnName = cell.getAttribute('data-column');
                if (columnName) {
                    const cellContent = cell.textContent.toLowerCase().replace(/-/g, '');
                    const searchQuery = query.replace(/-/g, '');
                    if (cellContent.includes(searchQuery)) {
                        found = true;
                        break;
                    }
                }
            }
            row.style.display = found ? 'table-row' : 'none';
        }
    }

    function resetTableDisplay(tableId) {
        const tableRows = document.getElementById(tableId).getElementsByTagName('tr');
        for (const row of tableRows) {
            row.style.display = 'table-row';
        }
    }

    document.getElementById('search-input-patient').addEventListener('input', function () {
        const query = this.value.trim().toLowerCase();
        resetTableDisplay('patientTable');
        handleSearchInput('patientTable', query);
    });

    document.getElementById('search-input-admin').addEventListener('input', function () {
        const query = this.value.trim().toLowerCase();
        resetTableDisplay('adminTable');
        handleSearchInput('adminTable', query);
    });

    document.getElementById('search-input-doctor').addEventListener('input', function () {
        const query = this.value.trim().toLowerCase();
        resetTableDisplay('doctorTable');
        handleSearchInput('doctorTable', query);
    });

    document.getElementById('search-input-pharmacist').addEventListener('input', function () {
        const query = this.value.trim().toLowerCase();
        resetTableDisplay('pharmacistTable');
        handleSearchInput('pharmacistTable', query);
    });

    document.getElementById('search-input-clinicassistant').addEventListener('input', function () {
        const query = this.value.trim().toLowerCase();
        resetTableDisplay('clinicAssistantTable');
        handleSearchInput('clinicAssistantTable', query);
    });

    // Assigned patient filter
    const filterDropdown = document.getElementById("filterAssigned");
    const rows = document.querySelectorAll("#assignedPatientTable tr");

    filterDropdown.addEventListener("change", function () {
        const value = this.value;

        rows.forEach(row => {
            const doctorCell = row.querySelector("td:nth-child(2)");
            if (!doctorCell) return;
            const doctorText = doctorCell.textContent.trim().toLowerCase();

            if (value === "all") {
                row.style.display = "";
            } else if (value === "assigned" && doctorText !== "not assigned") {
                row.style.display = "";
            } else if (value === "unassigned" && doctorText === "not assigned") {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });
});