using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text.Encodings.Web;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using _2021_dotnet_g_01.Models.Domain;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.UI.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.Extensions.Logging;

namespace _2021_dotnet_g_01.Areas.Identity.Pages.Account
{
    [AllowAnonymous]
    public class LoginModel : PageModel
    {
        private readonly UserManager<Customer> _userManager;
        private readonly SignInManager<Customer> _signInManager;
        private readonly ILogger<LoginModel> _logger;
        private readonly ICustomerRepository _customerRepository;
        private readonly ILoginAttemptRepository _loginAttemptRepository;

        public LoginModel(SignInManager<Customer> signInManager, 
            ILogger<LoginModel> logger,
            UserManager<Customer> userManager,
            ICustomerRepository customerRepository,
            ILoginAttemptRepository loginAttemptRepository)
        {
            _userManager = userManager;
            _signInManager = signInManager;
            _logger = logger;
            _customerRepository = customerRepository;
            _loginAttemptRepository = loginAttemptRepository;
        }

        [BindProperty]
        public InputModel Input { get; set; }

        public IList<AuthenticationScheme> ExternalLogins { get; set; }

        public string ReturnUrl { get; set; }

        [TempData]
        public string ErrorMessage { get; set; }

        public class InputModel
        {
            [Required]
            [EmailAddress]
            public string Email { get; set; }

            [Required]
            [DataType(DataType.Password)]
            public string Password { get; set; }

            [Display(Name = "Remember me?")]
            public bool RememberMe { get; set; }
        }

        public async Task OnGetAsync(string returnUrl = null)
        {
            if (!string.IsNullOrEmpty(ErrorMessage))
            {
                ModelState.AddModelError(string.Empty, ErrorMessage);
            }

            returnUrl = returnUrl ?? Url.Content("~/");

            // Clear the existing external cookie to ensure a clean login process
            await HttpContext.SignOutAsync(IdentityConstants.ExternalScheme);

            ExternalLogins = (await _signInManager.GetExternalAuthenticationSchemesAsync()).ToList();

            ReturnUrl = returnUrl;
        }

        public async Task<IActionResult> OnPostAsync(string returnUrl = null)
        {
            returnUrl = returnUrl ?? Url.Content("~/");
            Customer customer;

            if (ModelState.IsValid)
            {
                // This doesn't count login failures towards account lockout
                // To enable password failures to trigger account lockout, set lockoutOnFailure: true

                customer = _customerRepository.GetByCustomerName(Input.Email);
                var result = await _signInManager.PasswordSignInAsync(Input.Email, Input.Password, Input.RememberMe, lockoutOnFailure: false);

                if (result.Succeeded && (customer.Status == Status.Blocked || customer.Status == Status.Nonactive))
                {
                    _logger.LogWarning("User account locked out.");
                    await _signInManager.SignOutAsync();
                    return RedirectToPage("./Lockout");
                }
                
                if (result.Succeeded)
                {
                    _loginAttemptRepository.Add(new LoginAttempt(customer.Id, true));
                    _loginAttemptRepository.SaveChanges();
                    await _userManager.ResetAccessFailedCountAsync(customer);
                    _logger.LogInformation("User logged in.");
                    return LocalRedirect(returnUrl);
                }
                if (result.RequiresTwoFactor)
                {
                    return RedirectToPage("./LoginWith2fa", new { ReturnUrl = returnUrl, RememberMe = Input.RememberMe });
                }
                if (result.IsLockedOut)
                {
                    _loginAttemptRepository.Add(new LoginAttempt(customer.Id, false));
                    _loginAttemptRepository.SaveChanges();
                    _logger.LogWarning("User account locked out.");
                    return RedirectToPage("./Lockout");
                }
                else
                {
                    if (customer != null)
                    {
                        _loginAttemptRepository.Add(new LoginAttempt(customer.Id, false));
                        _loginAttemptRepository.SaveChanges();
                        await _userManager.AccessFailedAsync(customer);
                    }
                    ModelState.AddModelError(string.Empty, "Please check your login information.");
                    return Page();
                }
            }

            // If we got this far, something failed, redisplay form
            return Page();
        }
    }
}
