// Resource links bundled with each fallback step.
// Format: "Label — URL"  (the app displays them as bullet points)
const TECH_RESOURCES = {
  fundamentals: [
    'MDN Web Docs — https://developer.mozilla.org',
    'freeCodeCamp — https://www.freecodecamp.org'
  ],
  tooling: [
    'VS Code Docs — https://code.visualstudio.com/docs',
    'Node.js Getting Started — https://nodejs.org/en/docs/guides'
  ],
  architecture: [
    'Refactoring Guru (Design Patterns) — https://refactoring.guru/design-patterns',
    'roadmap.sh — https://roadmap.sh'
  ],
  api: [
    'REST API Tutorial — https://restfulapi.net',
    'Postman Learning Center — https://learning.postman.com'
  ],
  advanced: [
    'web.dev (Performance) — https://web.dev/performance',
    'JavaScript.info — https://javascript.info'
  ],
  testing: [
    'Jest Docs — https://jestjs.io/docs/getting-started',
    'Testing Library — https://testing-library.com/docs'
  ],
  capstone: [
    'GitHub — https://github.com',
    'Vercel (Deploy) — https://vercel.com'
  ]
};

const ART_RESOURCES = {
  foundations: [
    'Skillshare (Art Classes) — https://www.skillshare.com',
    'YouTube Art Tutorials — https://www.youtube.com/results?search_query=art+tutorial'
  ],
  precision: [
    'Proko (Drawing) — https://www.proko.com',
    'Pinterest (Pattern References) — https://www.pinterest.com'
  ],
  composition: [
    'Ctrl+Paint (Digital Art) — https://www.ctrlpaint.com',
    'Udemy Art Courses — https://www.udemy.com/courses/design-and-creativity'
  ],
  speed: [
    'Domestika — https://www.domestika.org',
    'ArtStation — https://www.artstation.com'
  ],
  portfolio: [
    'Behance — https://www.behance.net',
    'Instagram Portfolio Tips — https://www.instagram.com'
  ]
};

function generateSmartRoadmap(currentSkill, goal, experience) {
    const isDesignOrArt = /design|art|mehandi|drawing|painting|music|cooking|craft|photo|video|yoga|fashion|dance|beauty/i.test(goal + ' ' + currentSkill);

    let steps = [];
    if (isDesignOrArt) {
        steps = [
            {
                title: `Foundations & Essential Materials for ${currentSkill}`,
                description: `Understand basic techniques, material preparation, and sketching principles for ${goal}. Study reference images and gather your tools.`,
                resources: ART_RESOURCES.foundations,
                completed: false
            },
            {
                title: 'Mastering Precision, Lines & Symmetry',
                description: 'Practice core hand movements, intricate patterns, and structural layout techniques. Aim for 15–20 minutes of daily focused drills.',
                resources: ART_RESOURCES.precision,
                completed: false
            },
            {
                title: 'Advanced Composition & Creative Styles',
                description: `Explore contemporary styles, shading, depth, and unique artistic variations suited to ${goal}. Study masters in the field.`,
                resources: ART_RESOURCES.composition,
                completed: false
            },
            {
                title: 'Speed, Flow & Practical Execution',
                description: 'Build muscle memory, work efficiently under time constraints, and refine your technique across varied subjects.',
                resources: ART_RESOURCES.speed,
                completed: false
            },
            {
                title: `Portfolio & Masterpiece Project`,
                description: `Create a professional portfolio or showcase piece demonstrating your ${goal} mastery. Document your progress for clients or social media.`,
                resources: ART_RESOURCES.portfolio,
                completed: false
            }
        ];
    } else {
        steps = [
            {
                title: `${currentSkill} Refresher & Core Fundamentals`,
                description: `Review foundational concepts of ${currentSkill} — syntax, data structures, and best practices for a ${experience} learner heading toward ${goal}.`,
                resources: TECH_RESOURCES.fundamentals,
                completed: false
            },
            {
                title: 'Development Environment & Tooling Setup',
                description: `Configure your workspace, install CLI tools, linters, and all dependencies required for ${goal}. Version control with Git is essential from day one.`,
                resources: TECH_RESOURCES.tooling,
                completed: false
            },
            {
                title: 'Core Architecture & Intermediate Patterns',
                description: `Master data flow, state management, component design, and structural patterns directly relevant to ${goal}. Build small focused projects.`,
                resources: TECH_RESOURCES.architecture,
                completed: false
            },
            {
                title: 'API Integration & Asynchronous Workflows',
                description: 'Learn to fetch data from REST APIs, handle network errors gracefully, and manage asynchronous state — a must-have skill for any modern application.',
                resources: TECH_RESOURCES.api,
                completed: false
            },
            {
                title: 'Advanced Techniques & Optimization',
                description: `Explore performance tuning, lazy loading, caching strategies, and robust error handling patterns used by professional ${goal} developers.`,
                resources: TECH_RESOURCES.advanced,
                completed: false
            },
            {
                title: 'Testing, Debugging & Quality Assurance',
                description: 'Write unit tests and integration tests. Debug common issues using browser/IDE DevTools. Aim for a project with ≥70% test coverage.',
                resources: TECH_RESOURCES.testing,
                completed: false
            },
            {
                title: `Capstone Project — Build a Real-World ${goal} App`,
                description: `Apply everything by planning, designing, building, and deploying a complete ${goal} project. Push to GitHub and share your work.`,
                resources: TECH_RESOURCES.capstone,
                completed: false
            }
        ];
    }

    return {
        title: `Roadmap: ${currentSkill} ➔ ${goal}`,
        description: `Personalized ${experience}-level learning path from ${currentSkill} to ${goal}. Each step includes curated resources.`,
        steps
    };
}

async function generateGeminiRoadmap(currentSkill, goal, experience, apiKey) {
    const prompt = `Create a highly specialized, practical, and personalized learning roadmap for a ${experience} learner who currently knows "${currentSkill}" and wants to master "${goal}". Ensure the roadmap is completely domain-specific (e.g. if it is a design, art, or creative skill like Mehandi Design, tailor it to artistic techniques and practice; if it is technical, tailor it to software engineering). Return a JSON object with a concise "title", a helpful "description", and a "steps" array (between 5 to 7 steps). Each step must have a "title", a useful "description", a "resources" array of 2-3 strings in the format "Label — URL" pointing to real free learning resources (official docs, YouTube, freeCodeCamp, MDN, etc.), and "completed": false.`;

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 30000);

    try {
        const response = await fetch(
            'https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'x-goog-api-key': apiKey
                },
                body: JSON.stringify({
                    contents: [{ parts: [{ text: prompt }] }],
                    generationConfig: { responseMimeType: 'application/json' }
                }),
                signal: controller.signal
            }
        );

        if (!response.ok) {
            const errorBody = await response.text();
            let message = `Gemini API returned HTTP ${response.status}`;
            try {
                const parsedError = JSON.parse(errorBody);
                const detail = parsedError.error?.message;
                message += detail ? `: ${detail}` : `: ${errorBody.slice(0, 300)}`;
            } catch {
                message += `: ${errorBody.slice(0, 300)}`;
            }
            throw new Error(message);
        }

        const result = await response.json();
        const content = result.candidates?.[0]?.content?.parts
            ?.map(part => part.text || '')
            .join('')
            .trim();

        if (!content) throw new Error('Gemini returned an empty response');

        const roadmap = JSON.parse(content);
        if (
            typeof roadmap.title !== 'string' ||
            typeof roadmap.description !== 'string' ||
            !Array.isArray(roadmap.steps) ||
            roadmap.steps.length === 0 ||
            !roadmap.steps.every(s => s && typeof s.title === 'string' && typeof s.description === 'string')
        ) {
            throw new Error('Gemini returned a roadmap with an invalid format');
        }

        roadmap.steps = roadmap.steps.map(step => ({
            title: step.title,
            description: step.description,
            resources: Array.isArray(step.resources) ? step.resources.filter(r => typeof r === 'string') : [],
            completed: false
        }));
        return roadmap;
    } finally {
        clearTimeout(timeout);
    }
}

exports.generateRoadmap = async (req, res) => {
    try {
        const { currentSkill, goal, experience } = req.body || {};
        if (
            typeof currentSkill !== 'string' ||
            typeof goal !== 'string' ||
            !currentSkill.trim() ||
            !goal.trim() ||
            currentSkill.length > 100 ||
            goal.length > 100
        ) {
            return res.status(400).json({
                message: 'Current skill and goal are required and must be 100 characters or fewer'
            });
        }

        const exp = typeof experience === 'string' && experience.trim() ? experience.trim() : 'Beginner';
        const apiKey = process.env.GEMINI_API_KEY;

        if (apiKey) {
            try {
                const roadmap = await generateGeminiRoadmap(currentSkill.trim(), goal.trim(), exp, apiKey);
                return res.json(roadmap);
            } catch (aiErr) {
                console.error('Gemini API error, falling back to smart generator:', aiErr.message);
            }
        }

        return res.json(generateSmartRoadmap(currentSkill.trim(), goal.trim(), exp));
    } catch (err) {
        console.error('Generate roadmap error:', err.message);
        const { currentSkill, goal, experience } = req.body || {};
        const exp = typeof experience === 'string' && experience.trim() ? experience.trim() : 'Beginner';
        return res.json(generateSmartRoadmap(
            typeof currentSkill === 'string' ? currentSkill.trim() : 'Skill',
            typeof goal === 'string' ? goal.trim() : 'Goal',
            exp
        ));
    }
};
