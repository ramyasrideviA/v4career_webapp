import re

def process_file():
    admin_home_path = r'c:\Users\ADMIN\Documents\AI focuz project\learningarea\src\main\resources\templates\admin\home.html'
    employee_home_path = r'c:\Users\ADMIN\Documents\AI focuz project\learningarea\src\main\resources\templates\employee\home.html'
    
    with open(admin_home_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace basic strings
    content = content.replace('/admin/', '/employee/')
    content = content.replace('@{/admin', '@{/employee')
    content = content.replace('Admin Dashboard', 'Employee Dashboard')
    content = content.replace('Admin Portal', 'Employee Portal')
    
    # Remove the Daily Report sub-menu completely
    content = re.sub(r'<li class="report-group">.*?Daily Report.*?</li>', '', content, flags=re.DOTALL)
    # Alternatively, target just the daily report a tag specifically
    content = re.sub(r'<li>\s*<a th:href="@{/employee/home\?content=all-day-report}".*?Daily Report.*?</a>\s*</li>', '', content, flags=re.DOTALL)
    
    # Remove the Payment menu completely
    content = re.sub(r'<li class="has-submenu">\s*<a href="#" class="menu-item">\s*<i class="fas fa-credit-card"></i>\s*<span>Payment</span>.*?</li>', '', content, flags=re.DOTALL)
    
    # Remove the Accounts menu completely
    content = re.sub(r'<li class="has-submenu">\s*<a href="#" class="menu-item">\s*<i class="fas fa-piggy-bank"></i>.*?</li>', '', content, flags=re.DOTALL)

    # Remove the specific cards (Total Revenue, Total Profit) by looking for their col-lg-3 containers
    content = re.sub(r'<div class="col-lg-3 col-md-6">\s*<div class="card card-revenue h-100">.*?</div>\s*</div>', '', content, flags=re.DOTALL)
    content = re.sub(r'<div class="col-lg-3 col-md-6">\s*<div class="card card-profit h-100">.*?</div>\s*</div>', '', content, flags=re.DOTALL)

    # Remove complete th:if blocks for forbidden content
    unwanted_contents = [
        'all-payments', 'add-payment', 'all-transactions', 'add-income', 
        'add-expense', 'all-day-report', 'add-daily-report'
    ]
    
    for block in unwanted_contents:
        pattern = r'<div th:if="\${content == \'' + block + r'\'\}">.*?</div>\s*(?=<!--|<div th:if="\${content == |</div>\s*</div>)'
        # Let's use a simpler approach: splitting by th:if="\${content == " and filtering
        # Actually regex is fine if cautious
        content = re.sub(pattern, '', content, flags=re.DOTALL)

    # Clean up any leftover exact th:if blocks without greedy matching
    # Because sometimes they end tightly
    
    with open(employee_home_path, 'w', encoding='utf-8') as f:
        f.write(content)

if __name__ == '__main__':
    process_file()
    print("Done")
